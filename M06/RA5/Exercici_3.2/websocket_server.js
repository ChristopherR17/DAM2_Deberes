// Servidor WebSocket per a registre de moviments de jugador 2D amb MongoDB
// Autor: Christopher R. Carrillo Crespo
// Data: 07/04/2026

const WebSocket = require('ws');
const { MongoClient } = require('mongodb');
const winston = require('winston');
const crypto = require('crypto');

// ============================================
// CONFIGURACIÓ
// ============================================
const PORT = 8080;
const MONGO_URI = 'mongodb://root:password@localhost:27017';
const DB_NAME = 'game_db';
const COLLECTION_NAME = 'movements';
const INACTIVITY_LIMIT = 10000; // milisegundos -> 10s

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

function calculateDistance(point1, point2) {
  const dx = point2.x - point1.x;
  const dy = point2.y - point1.y;
  return Math.sqrt(dx * dx + dy * dy);
}

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

async function connectToMongo() {
  try {
    logger.info('Intentant connectar a MongoDB...');
    const client = new MongoClient(MONGO_URI);
    await client.connect();
    logger.info('Connectat a MongoDB correctament');
    
    db = client.db(DB_NAME);
    movementsCollection = db.collection(COLLECTION_NAME);
    
    await movementsCollection.createIndex({ sessionId: 1, timestamp: -1 });
    await movementsCollection.createIndex({ playerId: 1, serverTimestamp: -1 });
    
    logger.info(`Col·lecció '${COLLECTION_NAME}' preparada a la BD '${DB_NAME}'`);
    
    return client;
  } catch (error) {
    logger.error('Error connectant a MongoDB:', error);
    process.exit(1);
  }
}

async function saveMovement(movementData) {
  try {
    const documentToSave = {
      ...movementData,
      _id: crypto.randomUUID(),
      serverTimestamp: new Date(),
      serverTimestampEpoch: Date.now()
    };
    
    const result = await movementsCollection.insertOne(documentToSave);
    logger.debug(`Moviment guardat a MongoDB amb ID: ${result.insertedId}`);
    return result;
  } catch (error) {
    logger.error('Error guardant moviment a MongoDB:', error);
    throw error;
  }
}

// ============================================
// CLASSE PER GESTIONAR SESSIONS DE JUGADOR
// ============================================

class PlayerSession {
  constructor(playerId, ws) {
    this.playerId = playerId;
    this.sessionId = crypto.randomUUID();
    this.ws = ws;
    this.positions = [];
    this.lastMoveTime = Date.now();
    this.isActive = true;
    this.timeoutId = null;
    this.startTime = Date.now();
    
    logger.info(`Nova sessió iniciada`, {
      sessionId: this.sessionId,
      playerId: playerId,
      startTime: new Date(this.startTime).toISOString()
    });
  }
  
  updatePosition(position) {
    const now = Date.now();
    
    if (!this.isActive) {
      logger.info(`Reactivant sessió per jugador ${this.playerId}`);
      this.resetSession();
    }
    
    if (this.positions.length === 0) {
      this.positions.push({ x: position.x, y: position.y, timestamp: now, type: 'initial' });
      logger.debug(`Posició inicial enregistrada: (${position.x}, ${position.y})`);
    } else {
      this.positions.push({ x: position.x, y: position.y, timestamp: now, type: 'movement' });
    }
    
    this.lastMoveTime = now;
    this.resetInactivityTimer();
    
    logger.debug(`Jugador ${this.playerId} moviment a (${position.x}, ${position.y})`);
  }
  
  resetInactivityTimer() {
    if (this.timeoutId) clearTimeout(this.timeoutId);
    
    this.timeoutId = setTimeout(() => this.endSession(), INACTIVITY_LIMIT);
  }
  
  endSession() {
    if (!this.isActive) return;
    
    this.isActive = false;
    const sessionDuration = (Date.now() - this.startTime) / 1000;
    
    logger.info(`Finalitzant sessió per inactivitat`, {
      sessionId: this.sessionId,
      playerId: this.playerId,
      duration: `${sessionDuration.toFixed(2)}s`,
      totalMovements: this.positions.length
    });
    
    if (this.positions.length >= 2) {
      const startPos = this.positions[0];
      const endPos = this.positions[this.positions.length - 1];
      const distance = calculateDistance(startPos, endPos);
      const roundedDistance = Math.round(distance * 100) / 100;
      
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
          logger.info(`Missatge GAME_OVER enviat al client`, {
            distance: roundedDistance,
            startPos: `(${startPos.x}, ${startPos.y})`,
            endPos: `(${endPos.x}, ${endPos.y})`
          });
        }
      } catch (error) {
        logger.error('Error enviant missatge GAME_OVER:', error);
      }
      
      this.saveSessionStats(startPos, endPos, roundedDistance, sessionDuration);
      
    } else {
      logger.info(`Sessió finalitzada sense prou moviments per calcular distància`);
    }
    
    activeSessions.delete(this.playerId);
  }
  
  async saveSessionStats(startPos, endPos, distance, duration) {
    try {
      const statsDocument = {
        type: 'session_stats',
        sessionId: this.sessionId,
        playerId: this.playerId,
        startPosition: startPos,
        endPosition: endPos,
        distance,
        duration,
        totalMovements: this.positions.length,
        timestamp: new Date()
      };
      
      await movementsCollection.insertOne(statsDocument);
      logger.debug(`Estadístiques de sessió guardades`);
    } catch (error) {
      logger.error('Error guardant estadístiques:', error);
    }
  }
  
  resetSession() {
    const oldSessionId = this.sessionId;
    this.sessionId = crypto.randomUUID();
    this.positions = [];
    this.lastMoveTime = Date.now();
    this.startTime = Date.now();
    this.isActive = true;
    logger.info(`Sessió reiniciada`, {
      oldSessionId,
      newSessionId: this.sessionId,
      playerId: this.playerId
    });
    this.resetInactivityTimer();
  }
}

// ============================================
// SERVIDOR WEBSOCKET PRINCIPAL
// ============================================

async function startServer() {
  try {
    logger.info('Iniciant servidor WebSocket...');
    
    await connectToMongo();
    
    const wss = new WebSocket.Server({ port: PORT });
    logger.info(`Servidor WebSocket escoltant a ws://localhost:${PORT}`);
    logger.info(`Límit d'inactivitat: ${INACTIVITY_LIMIT/1000} segons`);
    
    wss.on('connection', (ws, req) => {
      const clientIp = req.socket.remoteAddress;
      logger.info(`Nou client connectat desde ${clientIp}`);
      
      ws.on('message', async (message) => {
        try {
          let data;
          try {
            data = JSON.parse(message.toString());
          } catch (parseError) {
            logger.warn(`Missatge no JSON rebut: ${message.toString().substring(0, 50)}`);
            return;
          }
          
          logger.debug('Missatge rebut:', data);
          
          if (!validateMovementMessage(data)) {
            logger.warn('Missatge mal format:', data);
            ws.send(JSON.stringify({ type: 'ERROR', message: 'Format de missatge invàlid' }));
            return;
          }
          
          const { playerId, position } = data;
          let session = activeSessions.get(playerId);
          if (!session) {
            session = new PlayerSession(playerId, ws);
            activeSessions.set(playerId, session);
            logger.info(`Nou jugador registrat: ${playerId}`);
          } else if (session.ws !== ws) {
            logger.warn(`Jugador ${playerId} connectat amb un altre WebSocket`);
          }
          
          session.updatePosition(position);
          await saveMovement({
            playerId,
            sessionId: session.sessionId,
            position,
            clientTimestamp: data.timestamp || Date.now(),
            movementNumber: session.positions.length
          });
          
        } catch (error) {
          logger.error('Error processant missatge:', error);
          if (ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify({ type: 'ERROR', message: 'Error intern del servidor' }));
          }
        }
      });
      
      ws.on('close', (code, reason) => {
        logger.info(`Client desconnectat`, { code, reason: reason.toString() });
        for (const [playerId, session] of activeSessions.entries()) {
          if (session.ws === ws) {
            logger.info(`Finalitzant sessió per desconnexió: ${playerId}`);
            session.endSession();
            activeSessions.delete(playerId);
          }
        }
      });
      
      ws.on('error', (error) => {
        logger.error('Error en WebSocket:', error);
      });
      
      ws.send(JSON.stringify({
        type: 'WELCOME',
        message: 'Connectat al servidor de joc',
        serverTime: Date.now(),
        inactivityLimit: INACTIVITY_LIMIT
      }));
    });
    
    wss.on('error', (error) => {
      logger.error('Error en servidor WebSocket:', error);
    });
    
    process.on('SIGINT', () => {
      logger.info('Tancant servidor...');
      logger.info(`Finalitzant ${activeSessions.size} sessions actives...`);
      for (const [playerId, session] of activeSessions.entries()) {
        session.endSession();
      }
      logger.info('Servidor aturat correctament');
      process.exit(0);
    });
    
    process.on('uncaughtException', (error) => {
      logger.error('Excepció no capturada:', error);
    });
    
    process.on('unhandledRejection', (reason, promise) => {
      logger.error('Promise rebutjada no gestionada:', { reason, promise });
    });
    
  } catch (error) {
    logger.error('Error fatal iniciant servidor:', error);
    process.exit(1);
  }
}

startServer();

module.exports = { startServer, PlayerSession, calculateDistance };