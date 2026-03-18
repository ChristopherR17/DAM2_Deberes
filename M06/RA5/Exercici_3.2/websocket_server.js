// websocket_server.js
// Servidor WebSocket per a registre de moviments de jugador 2D amb MongoDB
// Autor: [El teu nom]
// Data: [Data actual]

const WebSocket = require('ws');
const { MongoClient } = require('mongodb');
const winston = require('winston');
const crypto = require('crypto');

// ============================================
// CONFIGURACIÓ
// ============================================
const PORT = 8080;
const MONGO_URI = 'mongodb://localhost:27017';
const DB_NAME = 'game_db';
const COLLECTION_NAME = 'movements';
const INACTIVITY_LIMIT = 10000; // 10 segons en milisegons

// ============================================
// CONFIGURACIÓ DE WINSTON (LOGS)
// ============================================
const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    winston.format.errors({ stack: true }),
    winston.format.splat(),
    winston.format.json()
  ),
  defaultMeta: { service: 'game-server' },
  transports: [
    // Log a consola amb format llegible
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.printf(({ timestamp, level, message, ...meta }) => {
          return `[${timestamp}] ${level}: ${message} ${
            Object.keys(meta).length && meta.service ? '' : JSON.stringify(meta)
          }`;
        })
      )
    }),
    // Log d'errors a fitxer separat
    new winston.transports.File({ 
      filename: 'logs/error.log', 
      level: 'error',
      maxsize: 5242880, // 5MB
      maxFiles: 5
    }),
    // Log combinat de tots els nivells
    new winston.transports.File({ 
      filename: 'logs/combined.log',
      maxsize: 5242880, // 5MB
      maxFiles: 5
    })
  ]
});

// ============================================
// VARIABLES GLOBALS
// ============================================
let db;
let movementsCollection;
const activeSessions = new Map(); // Guarda les sessions actives per jugador

// ============================================
// FUNCIONS AUXILIARS
// ============================================

/**
 * Calcula la distància euclidiana entre dos punts 2D
 * @param {Object} point1 - Primer punt {x, y}
 * @param {Object} point2 - Segon punt {x, y}
 * @returns {number} Distància arrodonida a 2 decimals
 */
function calculateDistance(point1, point2) {
  const dx = point2.x - point1.x;
  const dy = point2.y - point1.y;
  return Math.sqrt(dx * dx + dy * dy);
}

/**
 * Valida l'estructura d'un missatge de moviment
 * @param {Object} data - Missatge rebut
 * @returns {boolean} True si és vàlid
 */
function validateMovementMessage(data) {
  if (!data || typeof data !== 'object') return false;
  if (!data.playerId || typeof data.playerId !== 'string') return false;
  if (!data.position || typeof data.position !== 'object') return false;
  if (typeof data.position.x !== 'number' || typeof data.position.y !== 'number') return false;
  
  return true;
}

// ============================================
// CONNEXIÓ A MONGODB
// ============================================

/**
 * Estableix connexió amb MongoDB i prepara la col·lecció
 * @returns {Promise<MongoClient>} Client de MongoDB
 */
async function connectToMongo() {
  try {
    logger.info('Intentant connectar a MongoDB...');
    const client = new MongoClient(MONGO_URI);
    await client.connect();
    logger.info('✅ Connectat a MongoDB correctament');
    
    db = client.db(DB_NAME);
    movementsCollection = db.collection(COLLECTION_NAME);
    
    // Crear índexs per optimitzar consultes
    await movementsCollection.createIndex({ sessionId: 1, timestamp: -1 });
    await movementsCollection.createIndex({ playerId: 1, serverTimestamp: -1 });
    
    logger.info(`✅ Col·lecció '${COLLECTION_NAME}' preparada a la BD '${DB_NAME}'`);
    
    return client;
  } catch (error) {
    logger.error('❌ Error connectant a MongoDB:', error);
    process.exit(1);
  }
}

/**
 * Guarda un moviment a MongoDB
 * @param {Object} movementData - Dades del moviment
 * @returns {Promise<Object>} Resultat de la inserció
 */
async function saveMovement(movementData) {
  try {
    // Afegir timestamp del servidor i ID únic
    const documentToSave = {
      ...movementData,
      _id: new crypto.randomUUID(), // ID únic per al document
      serverTimestamp: new Date(),
      serverTimestampEpoch: Date.now()
    };
    
    const result = await movementsCollection.insertOne(documentToSave);
    logger.debug(`💾 Moviment guardat a MongoDB amb ID: ${result.insertedId}`);
    return result;
  } catch (error) {
    logger.error('❌ Error guardant moviment a MongoDB:', error);
    throw error;
  }
}

// ============================================
// CLASSE PER GESTIONAR SESSIONS DE JUGADOR
// ============================================

class PlayerSession {
  /**
   * Crea una nova sessió de jugador
   * @param {string} playerId - Identificador del jugador
   * @param {WebSocket} ws - Connexió WebSocket associada
   */
  constructor(playerId, ws) {
    this.playerId = playerId;
    this.sessionId = crypto.randomUUID();
    this.ws = ws;
    this.positions = []; // Historial de posicions [ {x, y, timestamp} ]
    this.lastMoveTime = Date.now();
    this.isActive = true;
    this.timeoutId = null;
    this.startTime = Date.now();
    
    logger.info(`🆕 Nova sessió iniciada`, {
      sessionId: this.sessionId,
      playerId: playerId,
      startTime: new Date(this.startTime).toISOString()
    });
  }
  
  /**
   * Actualitza la posició del jugador
   * @param {Object} position - Nova posició {x, y}
   */
  updatePosition(position) {
    const now = Date.now();
    
    if (!this.isActive) {
      logger.info(`🔄 Reactivant sessió per jugador ${this.playerId}`);
      this.resetSession();
    }
    
    // Si és el primer moviment, guardar com a posició inicial
    if (this.positions.length === 0) {
      this.positions.push({ 
        x: position.x, 
        y: position.y, 
        timestamp: now,
        type: 'initial'
      });
      logger.debug(`🎯 Posició inicial enregistrada: (${position.x}, ${position.y})`);
    } else {
      // Guardar nova posició
      this.positions.push({ 
        x: position.x, 
        y: position.y, 
        timestamp: now,
        type: 'movement'
      });
    }
    
    this.lastMoveTime = now;
    
    // Reiniciar temporitzador d'inactivitat
    this.resetInactivityTimer();
    
    logger.debug(`🎮 Jugador ${this.playerId} moviment a (${position.x}, ${position.y})`);
  }
  
  /**
   * Reinicia el temporitzador d'inactivitat
   */
  resetInactivityTimer() {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
    
    this.timeoutId = setTimeout(() => {
      this.endSession();
    }, INACTIVITY_LIMIT);
  }
  
  /**
   * Finalitza la sessió per inactivitat
   */
  endSession() {
    if (!this.isActive) return;
    
    this.isActive = false;
    const sessionDuration = (Date.now() - this.startTime) / 1000; // en segons
    
    logger.info(`⏰ Finalitzant sessió per inactivitat`, {
      sessionId: this.sessionId,
      playerId: this.playerId,
      duration: `${sessionDuration.toFixed(2)}s`,
      totalMovements: this.positions.length
    });
    
    // Calcular distància si hi ha prou moviments
    if (this.positions.length >= 2) {
      const startPos = this.positions[0];
      const endPos = this.positions[this.positions.length - 1];
      const distance = calculateDistance(startPos, endPos);
      const roundedDistance = Math.round(distance * 100) / 100;
      
      // Enviar missatge de final de partida al client
      const gameOverMessage = {
        type: 'GAME_OVER',
        message: 'Partida finalitzada per inactivitat',
        distance: roundedDistance,
        startPosition: { x: startPos.x, y: startPos.y },
        endPosition: { x: endPos.x, y: endPos.y },
        totalMovements: this.positions.length,
        sessionDuration: sessionDuration
      };
      
      try {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          this.ws.send(JSON.stringify(gameOverMessage));
          logger.info(`📤 Missatge GAME_OVER enviat al client`, {
            distance: roundedDistance,
            startPos: `(${startPos.x}, ${startPos.y})`,
            endPos: `(${endPos.x}, ${endPos.y})`
          });
        }
      } catch (error) {
        logger.error('❌ Error enviant missatge GAME_OVER:', error);
      }
      
      // Guardar estadístiques de la sessió a MongoDB (opcional)
      this.saveSessionStats(startPos, endPos, roundedDistance, sessionDuration);
      
    } else {
      logger.info(`ℹ️ Sessió finalitzada sense prou moviments per calcular distància`);
    }
    
    // Eliminar sessió del map global
    activeSessions.delete(this.playerId);
  }
  
  /**
   * Guarda estadístiques de la sessió (opcional)
   */
  async saveSessionStats(startPos, endPos, distance, duration) {
    try {
      const statsDocument = {
        type: 'session_stats',
        sessionId: this.sessionId,
        playerId: this.playerId,
        startPosition: startPos,
        endPosition: endPos,
        distance: distance,
        duration: duration,
        totalMovements: this.positions.length,
        timestamp: new Date()
      };
      
      await movementsCollection.insertOne(statsDocument);
      logger.debug(`📊 Estadístiques de sessió guardades`);
    } catch (error) {
      logger.error('❌ Error guardant estadístiques:', error);
    }
  }
  
  /**
   * Reinicia la sessió (quan el jugador torna a moure's després de GAME_OVER)
   */
  resetSession() {
    const oldSessionId = this.sessionId;
    this.sessionId = crypto.randomUUID();
    this.positions = [];
    this.lastMoveTime = Date.now();
    this.startTime = Date.now();
    this.isActive = true;
    
    logger.info(`🔄 Sessió reiniciada`, {
      oldSessionId: oldSessionId,
      newSessionId: this.sessionId,
      playerId: this.playerId
    });
    
    this.resetInactivityTimer();
  }
}

// ============================================
// SERVIDOR WEBSOCKET PRINCIPAL
// ============================================

/**
 * Funció principal que inicia el servidor
 */
async function startServer() {
  try {
    logger.info('🚀 Iniciant servidor WebSocket...');
    
    // Connectar a MongoDB
    await connectToMongo();
    
    // Crear servidor WebSocket
    const wss = new WebSocket.Server({ port: PORT });
    
    logger.info(`✅ Servidor WebSocket escoltant a ws://localhost:${PORT}`);
    logger.info(`⏱️  Límit d'inactivitat: ${INACTIVITY_LIMIT/1000} segons`);
    
    // Gestionar noves connexions
    wss.on('connection', (ws, req) => {
      const clientIp = req.socket.remoteAddress;
      logger.info(`🔌 Nou client connectat desde ${clientIp}`);
      
      // Gestionar missatges rebuts
      ws.on('message', async (message) => {
        try {
          // Intentar parsejar el missatge JSON
          let data;
          try {
            data = JSON.parse(message.toString());
          } catch (parseError) {
            logger.warn(`📦 Missatge no JSON rebut: ${message.toString().substring(0, 50)}`);
            return;
          }
          
          logger.debug('📨 Missatge rebut:', data);
          
          // Validar estructura del missatge
          if (!validateMovementMessage(data)) {
            logger.warn('⚠️ Missatge mal format:', data);
            
            // Enviar error al client
            ws.send(JSON.stringify({
              type: 'ERROR',
              message: 'Format de missatge invàlid'
            }));
            return;
          }
          
          const { playerId, position } = data;
          
          // Obtenir o crear sessió per aquest jugador
          let session = activeSessions.get(playerId);
          if (!session) {
            session = new PlayerSession(playerId, ws);
            activeSessions.set(playerId, session);
            logger.info(`👤 Nou jugador registrat: ${playerId}`);
          } else if (session.ws !== ws) {
            // Si el mateix jugador es connecta amb un WebSocket diferent
            logger.warn(`⚠️ Jugador ${playerId} connectat amb un altre WebSocket`);
          }
          
          // Actualitzar posició
          session.updatePosition(position);
          
          // Guardar moviment a MongoDB
          await saveMovement({
            playerId,
            sessionId: session.sessionId,
            position,
            clientTimestamp: data.timestamp || Date.now(),
            movementNumber: session.positions.length
          });
          
        } catch (error) {
          logger.error('❌ Error processant missatge:', error);
          
          // Notificar error al client
          if (ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify({
              type: 'ERROR',
              message: 'Error intern del servidor'
            }));
          }
        }
      });
      
      // Gestionar tancament de connexió
      ws.on('close', (code, reason) => {
        logger.info(`🔴 Client desconnectat`, { code, reason: reason.toString() });
        
        // Finalitzar sessions associades a aquest WebSocket
        for (const [playerId, session] of activeSessions.entries()) {
          if (session.ws === ws) {
            logger.info(`👋 Finalitzant sessió per desconnexió: ${playerId}`);
            session.endSession();
            activeSessions.delete(playerId);
          }
        }
      });
      
      // Gestionar errors de WebSocket
      ws.on('error', (error) => {
        logger.error('❌ Error en WebSocket:', error);
      });
      
      // Enviar missatge de benvinguda
      ws.send(JSON.stringify({
        type: 'WELCOME',
        message: 'Connectat al servidor de joc',
        serverTime: Date.now(),
        inactivityLimit: INACTIVITY_LIMIT
      }));
    });
    
    // Gestionar errors del servidor
    wss.on('error', (error) => {
      logger.error('❌ Error en servidor WebSocket:', error);
    });
    
    // Gestionar tancament del servidor (Ctrl+C)
    process.on('SIGINT', () => {
      logger.info('📴 Tancant servidor...');
      
      // Finalitzar totes les sessions actives
      logger.info(`📊 Finalitzant ${activeSessions.size} sessions actives...`);
      for (const [playerId, session] of activeSessions.entries()) {
        session.endSession();
      }
      
      logger.info('👋 Servidor aturat correctament');
      process.exit(0);
    });
    
    // Gestionar errors no capturats
    process.on('uncaughtException', (error) => {
      logger.error('💥 Excepció no capturada:', error);
    });
    
    process.on('unhandledRejection', (reason, promise) => {
      logger.error('💥 Promise rebutjada no gestionada:', { reason, promise });
    });
    
  } catch (error) {
    logger.error('❌ Error fatal iniciant servidor:', error);
    process.exit(1);
  }
}

// ============================================
// INICIAR SERVIDOR
// ============================================
startServer();

// Exportar per possibles tests (opcional)
module.exports = { startServer, PlayerSession, calculateDistance };