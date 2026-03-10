// exercici2.js
const { MongoClient } = require('mongodb');
const PDFDocument = require('pdfkit');
const fs = require('fs');
const path = require('path');

// Configuració MongoDB
const MONGODB_URI = 'mongodb://root:password@localhost:27017';
const DB_NAME = 'stackexchange';
const COLLECTION_NAME = 'preguntes';

// Paraules a cercar al títol
const PARAULES = ["pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf"];

async function connectToMongo() {
    try {
        const client = new MongoClient(MONGODB_URI);
        await client.connect();
        console.log('Connectat a MongoDB');
        return client;
    } catch (error) {
        console.error('Error connectant a MongoDB:', error.message);
        throw error;
    }
}

async function consultaMitjanaViewCount(collection) {
    console.log('\n--- Consulta 1: ViewCount superior a la mitjana ---');

    // Calcular la mitjana de ViewCount
    const resultatMitjana = await collection.aggregate([
        {
            $group: {
                _id: null,
                mitjana: 
                { 
                    $avg: 
                    { 
                        $toInt: "$question.ViewCount" 
                    } 
                }
            }
        }
    ]).toArray();

    const mitjana = resultatMitjana[0]?.mitjana || 0;
    console.log(`Mitjana de ViewCount: ${Math.round(mitjana)}`);

    // Trobar preguntes amb ViewCount > mitjana
    const resultats = await collection.find({
        $expr: {
            $gt: [
                { $toInt: "$question.ViewCount" },
                mitjana
            ]
        }
    }).toArray();

    console.log(`Trobades ${resultats.length} preguntes`);
    return resultats;
}

async function consultaParaulesTitol(collection) {
    console.log('\n--- Consulta 2: Paraules específiques al títol ---');

    // Construir condicions per a cada paraula
    const conditions = PARAULES.map(paraula => ({
        "question.Title": { $regex: paraula, $options: "i" }
    }));

    const resultats = await collection.find({
        $or: conditions
    }).toArray();

    console.log(`Trobades ${resultats.length} preguntes`);
    console.log('Paraules cercades:', PARAULES.join(', '));

    return resultats;
}

async function generarPDF(titol, resultats, nomFitxer) {
    return new Promise((resolve, reject) => {
        try {
            const doc = new PDFDocument();
            const stream = fs.createWriteStream(path.join(__dirname, 'data/out', nomFitxer));

            doc.pipe(stream);

            // Títol del document
            doc.fontSize(16).text(titol, { align: 'center' });
            doc.moveDown();

            // Data de generació
            doc.fontSize(10).text('Data generació: ' + new Date().toLocaleDateString('ca-ES'));
            doc.moveDown();

            // Total de resultats
            doc.fontSize(12).text('Total de resultats: ' + resultats.length);
            doc.moveDown();

            // Llistar els títols
            doc.fontSize(10);

            resultats.forEach((resultat, index) => {
                const titol = resultat.question.Title || 'Sense títol';
                const views = resultat.question.ViewCount || '0';

                doc.text(`${index + 1}. ${titol} (${views} visites)`);

                // Control de pàgina
                if ((index + 1) % 40 === 0) {
                    doc.addPage();
                }
            });

            doc.end();

            stream.on('finish', () => {
                console.log(`PDF generat: ${nomFitxer}`);
                resolve();
            });

            stream.on('error', reject);

        } catch (error) {
            reject(error);
        }
    });
}

async function main() {
    console.log('Iniciant exercici 2');

    const client = await connectToMongo();

    try {
        const db = client.db(DB_NAME);
        const collection = db.collection(COLLECTION_NAME);

        // Consulta 1: ViewCount > mitjana
        const resultats1 = await consultaMitjanaViewCount(collection);
        await generarPDF(
            'Preguntes amb ViewCount superior a la mitjana',
            resultats1,
            'informe1.pdf'
        );

        // Consulta 2: Paraules al títol
        const resultats2 = await consultaParaulesTitol(collection);
        await generarPDF(
            'Preguntes amb paraules específiques al títol',
            resultats2,
            'informe2.pdf'
        );

        console.log('\nTots els informes generats a ./data/out/');

    } catch (error) {
        console.error('Error:', error);
    } finally {
        await client.close();
        console.log('Connexió a MongoDB tancada');
    }
}

main();