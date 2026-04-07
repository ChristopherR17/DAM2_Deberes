// websocket_client.js
const WebSocket = require('ws');
const readline = require('readline');

// Configurar readline per capturar tecles sense necessitat de Enter
readline.emitKeypressEvents(process.stdin);
process.stdin.setRawMode(true);

// Configuració
const SERVER_URL = 'ws://localhost:8080';
const PLAYER_ID = `player_${Math.floor(Math.random() * 1000)}`;

let ws;
let currentPosition = { x: 0, y: 0 };
let sessionActive = true;

// Funció per connectar al servidor
function connectToServer() {
  ws = new WebSocket(SERVER_URL);
  
  ws.on('open', () => {
    console.log(`\nConnectat al servidor com a ${PLAYER_ID}`);
    console.log('Utilitza les fletxes per moure el jugador');
    console.log('Prem "q" per sortir\n');
    console.log(`Posició actual: (${currentPosition.x}, ${currentPosition.y})`);
  });
  
  ws.on('message', (data) => {
    try {
      const message = JSON.parse(data.toString());
      
      if (message.type === 'GAME_OVER') {
        console.log('\nFINAL DE PARTIDA');
        console.log(`Distància recorreguda: ${message.distance}`);
        console.log(`Inici: (${message.startPosition.x}, ${message.startPosition.y})`);
        console.log(`Final: (${message.endPosition.x}, ${message.endPosition.y})`);
        console.log('\nMou una fletxa per iniciar nova partida...\n');
        
        sessionActive = false;
      }
    } catch (error) {
      console.error('Error processant missatge:', error);
    }
  });
  
  ws.on('close', () => {
    console.log('Desconnectat del servidor');
    process.exit(0);
  });
  
  ws.on('error', (error) => {
    console.error('Error en connexió:', error);
  });
}

// Funció per enviar posició al servidor
function sendPosition() {
  if (ws && ws.readyState === WebSocket.OPEN) {
    const message = {
      playerId: PLAYER_ID,
      position: currentPosition,
      timestamp: Date.now()
    };
    
    ws.send(JSON.stringify(message));
    
    if (!sessionActive) {
      sessionActive = true;
      console.log('Nova partida iniciada!');
    }
    
    console.log(`Posició enviada: (${currentPosition.x}, ${currentPosition.y})`);
  }
}

// Capturar tecles
process.stdin.on('keypress', (str, key) => {
  // Sortir amb 'q' o Ctrl+C
  if (key.name === 'q' || (key.ctrl && key.name === 'c')) {
    console.log('\nSortint...');
    ws.close();
    process.exit(0);
  }
  
  // Processar fletxes
  let moved = false;
  
  switch (key.name) {
    case 'up':
      currentPosition.y += 1;
      moved = true;
      break;
    case 'down':
      currentPosition.y -= 1;
      moved = true;
      break;
    case 'left':
      currentPosition.x -= 1;
      moved = true;
      break;
    case 'right':
      currentPosition.x += 1;
      moved = true;
      break;
  }
  
  if (moved) {
    console.log(`Nova posició: (${currentPosition.x}, ${currentPosition.y})`);
    sendPosition();
  }
});

// Iniciar client
console.log('Client de joc 2D');
connectToServer();