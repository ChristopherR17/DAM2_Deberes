const swaggerJsDoc = require('swagger-jsdoc');

const swaggerOptions = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Xat API',
            version: '1.0.0',
            description: 'API per gestionar converses i prompts'
        },
        servers: [
            {
                url: 'http://127.0.0.1:3000',
                description: 'Servidor de desenvolupament'
            }
        ]
    },
    apis: ['./src/routes/*.js']
};

module.exports = swaggerJsDoc(swaggerOptions);

/**
 * @swagger
 * /api/chat/sentiment-analysis:
 *   post:
 *     summary: Analyze text sentiment
 *     tags: [Chat]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - text
 *               - userId
 *             properties:
 *               text:
 *                 type: string
 *               userId:
 *                 type: string
 *               sessionId:
 *                 type: string
 *               language:
 *                 type: string
 *     responses:
 *       200:
 *         description: Sentiment analysis result
 *       400:
 *         description: Invalid input
 */
