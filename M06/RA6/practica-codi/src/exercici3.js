// Importacions
const fs = require('fs').promises;
const path = require('path');
require('dotenv').config();

// Constants
const IMAGES_SUBFOLDER = 'imatges/animals';
const IMAGE_TYPES = ['.jpg', '.jpeg', '.png', '.gif'];
const OLLAMA_URL = process.env.CHAT_API_OLLAMA_URL;
const OLLAMA_MODEL = process.env.CHAT_API_OLLAMA_MODEL_VISION;
const OUTPUT_FILE = path.join(__dirname, process.env.DATA_PATH, 'exercici3_resposta.json');

// Funció per llegir un fitxer i convertir-lo a Base64
async function imageToBase64(imagePath) {
    try {
        const data = await fs.readFile(imagePath);
        return Buffer.from(data).toString('base64');
    } catch (error) {
        console.error(`Error al llegir o convertir la imatge ${imagePath}:`, error.message);
        return null;
    }
}

// Funció per fer la petició a Ollama
async function queryOllama(base64Image, prompt) {
    const requestBody = {
        model: OLLAMA_MODEL,
        prompt: prompt,
        images: [base64Image],
        stream: false
    };

    try {
        const response = await fetch(`${OLLAMA_URL}/generate`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status} ${response.statusText}`);
        }

        // Depuració de la resposta
        const data = await response.json();

        // Verificar si tenim una resposta vàlida
        if (!data || !data.response) {
            throw new Error('La resposta d\'Ollama no té el format esperat');
        }

        return data.response;
    } catch (error) {
        console.error('Error detallat en la petició a Ollama:', error);
        return null;
    }
}

function cleanOllamaJSON(response) {
    const match = response.match(/```json\s*([\s\S]*?)```/);
    if (match) return match[1].trim();
    return response.trim();
}

// Funció principal
async function main() {
    try {
        // Validem les variables d'entorn necessàries
        if (!process.env.DATA_PATH) {
            throw new Error('La variable d\'entorn DATA_PATH no està definida.');
        }
        if (!OLLAMA_URL) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_URL no està definida.');
        }
        if (!OLLAMA_MODEL) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_MODEL no està definida.');
        }

        const imagesFolderPath = path.join(__dirname, process.env.DATA_PATH, IMAGES_SUBFOLDER);
        try {
            await fs.access(imagesFolderPath);
        } catch (error) {
            throw new Error(`El directori d'imatges no existeix: ${imagesFolderPath}`);
        }

        const animalDirectories = await fs.readdir(imagesFolderPath);
        const result = { analisis: [] };

        // Iterem per cada element dins del directori d'animals
        for (const animalDir of animalDirectories) {
            // Construïm la ruta completa al directori de l'animal actual
            const animalDirPath = path.join(imagesFolderPath, animalDir);
            const stats = await fs.stat(animalDirPath);
            if (!stats.isDirectory()) continue;

            const imageFiles = await fs.readdir(animalDirPath);

            for (const imageFile of imageFiles) {
                // Construïm la ruta completa al fitxer d'imatge
                const imagePath = path.join(animalDirPath, imageFile);
                // Obtenim l'extensió del fitxer i la convertim a minúscules
                const ext = path.extname(imagePath).toLowerCase();
                if (!IMAGE_TYPES.includes(ext)) continue;

                const base64String = await imageToBase64(imagePath);
                if (!base64String) continue;

                // Prompt detallat per obtenir tota la informació requerida
                const prompt = `
                    Analitza detalladament l'animal d'aquesta imatge i genera un JSON amb el següent format:
                    {
                        "nom_comu": "...",
                        "nom_cientific": "...",
                        "taxonomia": {
                            "classe": "...",
                            "ordre": "...",
                            "familia": "..."
                        },
                        "habitat": {
                            "tipus": ["..."],
                            "regioGeografica": ["..."],
                            "clima": ["..."]
                        },
                        "dieta": {
                            "tipus": "...",
                            "aliments_principals": ["..."]
                        },
                        "caracteristiques_fisiques": {
                            "mida": {
                                "altura_mitjana_cm": "...",
                                "pes_mitja_kg": "..."
                            },
                            "colors_predominants": ["..."],
                            "trets_distintius": ["..."]
                        },
                        "estat_conservacio": {
                            "classificacio_IUCN": "...",
                            "amenaces_principals": ["..."]
                        }
                    }
                    Retorna només el JSON complet sense text addicional.
                `;

                const response = await queryOllama(base64String, prompt);

                if (response) {
                    try {
                        const clean = cleanOllamaJSON(json);
                        const parsed = JSON.parse(clean);
                        result.analisis.push({
                            imatge: { nom_fitxer: imageFile },
                            analisi: parsed
                        });
                        console.log(`Analisi guardat per ${imageFile}`);
                    } catch (err) {
                        console.error(`Error parsejant JSON de ${imageFile}:`, err.message);
                    }
                }
            }

            // Per ara processem només el primer directori
            break;
        }

        // Guardar la resposta en un fitxer JSON
        await fs.writeFile(OUTPUT_FILE, JSON.stringify(result, null, 2));
        console.log(`\nResultat guardat a: ${OUTPUT_FILE}`);
    } catch (error) {
        console.error('Error durant l\'execució:', error.message);
    }
}

// Executar
main();
