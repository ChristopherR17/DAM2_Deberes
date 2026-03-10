// exercici1.js
const fs = require('fs');
const path = require('path');
const { MongoClient } = require('mongodb');
const xml2js = require('xml2js');
const entities = require('entities');
const winston = require('winston');

// Configuració del logger
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.printf(({ timestamp, level, message }) => {
            return `${timestamp} [${level.toUpperCase()}]: ${message}`;
        })
    ),
    transports: [
        new winston.transports.Console(),
        new winston.transports.File({ 
            filename: path.join(__dirname, 'data/logs/exercici1.log') 
        })
    ]
});

// Configuració MongoDB
const MONGODB_URI = 'mongodb://root:password@localhost:27017';
const DB_NAME = 'stackexchange';
const COLLECTION_NAME = 'preguntes';

async function connectToMongo() {
    try {
        const client = new MongoClient(MONGODB_URI);
        await client.connect();
        logger.info('Connectat a MongoDB correctament');
        return client;
    } catch (error) {
        logger.error('Error connectant a MongoDB: ' + error.message);
        throw error;
    }
}

async function processarXML(rutaFitxer) {
    return new Promise((resolve, reject) => {
        logger.info('Llegint fitxer: ' + rutaFitxer);
        
        fs.readFile(rutaFitxer, 'utf-8', (err, data) => {
            if (err) {
                reject(err);
                return;
            }

            const parser = new xml2js.Parser({ explicitArray: false });
            
            parser.parseString(data, (err, result) => {
                if (err) {
                    reject(err);
                    return;
                }

                try {
                    const rows = result?.posts?.row || result?.row || [];
                    
                    logger.info('Trobats ' + rows.length + ' registres totals al fitxer XML');
                    
                    const preguntes = rows
                        .filter(row => row.$.PostTypeId === '1')
                        .map(row => {
                            const bodyDecodificat = entities.decodeHTML(row.$.Body || '');
                            
                            return {
                                question: {
                                    Id: row.$.Id,
                                    PostTypeId: row.$.PostTypeId,
                                    AcceptedAnswerId: row.$.AcceptedAnswerId,
                                    CreationDate: row.$.CreationDate,
                                    Score: row.$.Score,
                                    ViewCount: row.$.ViewCount,
                                    Body: bodyDecodificat,
                                    OwnerUserId: row.$.OwnerUserId,
                                    LastActivityDate: row.$.LastActivityDate,
                                    Title: row.$.Title,
                                    Tags: row.$.Tags,
                                    AnswerCount: row.$.AnswerCount,
                                    CommentCount: row.$.CommentCount,
                                    ContentLicense: row.$.ContentLicense
                                }
                            };
                        });

                    logger.info('Filtrades ' + preguntes.length + ' preguntes (PostTypeId=1)');
                    
                    const topPreguntes = preguntes
                        .sort((a, b) => {
                            const viewsA = parseInt(a.question.ViewCount) || 0;
                            const viewsB = parseInt(b.question.ViewCount) || 0;
                            return viewsB - viewsA;
                        })
                        .slice(0, Math.min(10000, preguntes.length));

                    logger.info('Seleccionades les ' + topPreguntes.length + ' preguntes amb més visualitzacions');
                    
                    resolve(topPreguntes);
                } catch (error) {
                    reject(error);
                }
            });
        });
    });
}

async function main() {
    logger.info('Iniciant exercici 1');
    
    const client = await connectToMongo();
    
    try {
        const db = client.db(DB_NAME);
        const collection = db.collection(COLLECTION_NAME);
        
        await collection.drop().catch(() => {
            logger.info('No existia col·lecció prèvia');
        });
        
        const rutaXML = './data/Posts.xml';
        const preguntes = await processarXML(rutaXML);
        
        if (preguntes.length === 0) {
            logger.warn('No s\'han trobat preguntes per inserir');
            return;
        }
        
        logger.info('Inserint ' + preguntes.length + ' documents a MongoDB...');
        const resultat = await collection.insertMany(preguntes);
        logger.info('Inserides ' + resultat.insertedCount + ' preguntes a MongoDB');
        
        const count = await collection.countDocuments();
        logger.info('Total de documents a la col·lecció: ' + count);
        
    } catch (error) {
        logger.error('Error en l\'execució: ' + error.message);
    } finally {
        await client.close();
        logger.info('Connexió a MongoDB tancada');
        logger.info('Exercici 1 completat');
    }
}

main();