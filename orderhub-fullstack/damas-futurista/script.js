const BOARD_SIZE = 8;
const boardElement = document.getElementById('board');
const score1Element = document.getElementById('score1');
const score2Element = document.getElementById('score2');
const cyanTurnStatus = document.querySelector('.cyan-turn');
const magentaTurnStatus = document.querySelector('.magenta-turn');
const gameOverModal = document.getElementById('game-over');
const winnerText = document.getElementById('winner-text');
const restartBtn = document.getElementById('restart-btn');

let board = [];
// 1 = Player 1 (Cyan), 2 = Player 2 (Magenta), 3 = King 1, 4 = King 2
let currentPlayer = 1;
let selectedPiece = null; // {r, c}
let piecesP1 = 12;
let piecesP2 = 12;
let validMoves = []; // Array of move objects {r, c, isJump, jumpedRow, jumpedCol}

// Audio context setup for futuristic synth sounds
let audioCtx = null;

function initAudio() {
    if (!audioCtx) {
        audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (audioCtx.state === 'suspended') {
        audioCtx.resume();
    }
}

function playSound(type) {
    if (!audioCtx) return;
    
    const osc = audioCtx.createOscillator();
    const gainNode = audioCtx.createGain();
    
    osc.connect(gainNode);
    gainNode.connect(audioCtx.destination);
    
    if (type === 'move') {
        osc.type = 'sine';
        osc.frequency.setValueAtTime(400, audioCtx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(800, audioCtx.currentTime + 0.1);
        gainNode.gain.setValueAtTime(0.2, audioCtx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.15);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.15);
    } else if (type === 'jump') {
        osc.type = 'square';
        osc.frequency.setValueAtTime(300, audioCtx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(60, audioCtx.currentTime + 0.2);
        gainNode.gain.setValueAtTime(0.3, audioCtx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.25);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.25);
    } else if (type === 'king') {
        osc.type = 'triangle';
        osc.frequency.setValueAtTime(400, audioCtx.currentTime);
        osc.frequency.linearRampToValueAtTime(1200, audioCtx.currentTime + 0.4);
        gainNode.gain.setValueAtTime(0.4, audioCtx.currentTime);
        gainNode.gain.linearRampToValueAtTime(0.01, audioCtx.currentTime + 0.5);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.5);
    } else if (type === 'error') {
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(150, audioCtx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(100, audioCtx.currentTime + 0.1);
        gainNode.gain.setValueAtTime(0.2, audioCtx.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.1);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.1);
    }
}

function initGame() {
    board = Array(BOARD_SIZE).fill(null).map(() => Array(BOARD_SIZE).fill(0));
    currentPlayer = 1;
    selectedPiece = null;
    piecesP1 = 12;
    piecesP2 = 12;
    validMoves = [];
    
    // Setup initial pieces
    for (let r = 0; r < BOARD_SIZE; r++) {
        for (let c = 0; c < BOARD_SIZE; c++) {
            if ((r + c) % 2 === 1) {
                if (r < 3) board[r][c] = 2; // Player 2 (top)
                else if (r > 4) board[r][c] = 1; // Player 1 (bottom)
            }
        }
    }
    
    updateUI();
    renderBoard();
    gameOverModal.classList.add('hidden');
}

function updateUI() {
    score1Element.textContent = String(piecesP1).padStart(2, '0');
    score2Element.textContent = String(piecesP2).padStart(2, '0');
    
    if (currentPlayer === 1) {
        cyanTurnStatus.classList.add('active');
        magentaTurnStatus.classList.remove('active');
    } else {
        magentaTurnStatus.classList.add('active');
        cyanTurnStatus.classList.remove('active');
    }
}

function renderBoard() {
    boardElement.innerHTML = '';
    for (let r = 0; r < BOARD_SIZE; r++) {
        for (let c = 0; c < BOARD_SIZE; c++) {
            const square = document.createElement('div');
            const isDark = (r + c) % 2 === 1;
            square.className = `square ${isDark ? 'dark' : 'light'}`;
            square.dataset.r = r;
            square.dataset.c = c;
            
            if (isDark) {
                // Check if valid move target
                const moveObj = validMoves.find(m => m.r === r && m.c === c);
                if (moveObj) {
                    square.classList.add('valid');
                    square.addEventListener('click', () => handleSquareClick(r, c));
                } else {
                    square.addEventListener('click', () => {
                        if (board[r][c] !== 0) {
                            handlePieceClick(r, c);
                        } else {
                            // Clicou num espaço vazio sem ser jogada válida
                            if (selectedPiece) {
                                playSound('error');
                                selectedPiece = null;
                                validMoves = [];
                                renderBoard();
                            }
                        }
                    });
                }
            }
            
            const cellVal = board[r][c];
            if (cellVal !== 0) {
                const piece = document.createElement('div');
                piece.className = 'piece';
                if (cellVal === 1 || cellVal === 3) piece.classList.add('player1');
                if (cellVal === 2 || cellVal === 4) piece.classList.add('player2');
                if (cellVal === 3 || cellVal === 4) {
                    piece.classList.add('king');
                    const crown = document.createElement('div');
                    crown.className = 'crown-icon';
                    crown.innerHTML = '★'; // Utilizando caracter de estrela para dama
                    piece.appendChild(crown);
                }
                
                if (selectedPiece && selectedPiece.r === r && selectedPiece.c === c) {
                    piece.classList.add('selected');
                }
                
                // Add click event for pieces directly for better feedback
                piece.addEventListener('click', (e) => {
                    e.stopPropagation(); // Prevent square click
                    initAudio();
                    handlePieceClick(r, c);
                });
                
                square.appendChild(piece);
            }
            
            boardElement.appendChild(square);
        }
    }
}

function getValidMoves(player) {
    let allMoves = [];
    let hasJump = false;
    
    for (let r = 0; r < BOARD_SIZE; r++) {
        for (let c = 0; c < BOARD_SIZE; c++) {
            const val = board[r][c];
            if ((player === 1 && (val === 1 || val === 3)) || 
                (player === 2 && (val === 2 || val === 4))) {
                
                const pieceMoves = getPieceMoves(r, c, val);
                
                if (pieceMoves.some(m => m.isJump)) {
                    hasJump = true;
                }
                
                pieceMoves.forEach(m => {
                    allMoves.push({ ...m, fromR: r, fromC: c });
                });
            }
        }
    }
    
    // Captura Obrigatória
    if (hasJump) {
        allMoves = allMoves.filter(m => m.isJump);
    }
    
    return { allMoves, hasJump };
}

function getPieceMoves(r, c, val) {
    const moves = [];
    const isKing = val === 3 || val === 4;
    const dir = (val === 1 || val === 3) ? -1 : 1; // P1 (bottom) moves UP (-1), P2 (top) moves DOWN (+1)
    
    const directions = isKing ? [
        {dr: 1, dc: 1}, {dr: 1, dc: -1},
        {dr: -1, dc: 1}, {dr: -1, dc: -1}
    ] : [
        {dr: dir, dc: -1}, {dr: dir, dc: 1} 
    ];
    
    directions.forEach(d => {
        // Normal move
        const nr = r + d.dr;
        const nc = c + d.dc;
        if (nr >= 0 && nr < BOARD_SIZE && nc >= 0 && nc < BOARD_SIZE) {
            if (board[nr][nc] === 0) {
                moves.push({ r: nr, c: nc, isJump: false });
            } else if (isOpponent(val, board[nr][nc])) {
                // Check jump over opponent
                const jr = nr + d.dr;
                const jc = nc + d.dc;
                if (jr >= 0 && jr < BOARD_SIZE && jc >= 0 && jc < BOARD_SIZE && board[jr][jc] === 0) {
                    moves.push({ r: jr, c: jc, isJump: true, jumpedRow: nr, jumpedCol: nc });
                }
            }
        }
    });
    
    return moves;
}

function isOpponent(myVal, otherVal) {
    if (otherVal === 0) return false;
    const isMyP1 = myVal === 1 || myVal === 3;
    const isOtherP1 = otherVal === 1 || otherVal === 3;
    return isMyP1 !== isOtherP1;
}

function handlePieceClick(r, c) {
    const val = board[r][c];
    
    // Check if the clicked piece belongs to the current player
    if ((currentPlayer === 1 && (val === 1 || val === 3)) ||
        (currentPlayer === 2 && (val === 2 || val === 4))) {
        
        const { allMoves } = getValidMoves(currentPlayer);
        const pieceMoves = allMoves.filter(m => m.fromR === r && m.fromC === c);
        
        if (pieceMoves.length > 0) {
            playSound('move'); // small click sound
            selectedPiece = { r, c };
            validMoves = pieceMoves;
            renderBoard();
        } else {
            playSound('error'); // invalid piece selected or forced capture somewhere else
            if (selectedPiece && (selectedPiece.r !== r || selectedPiece.c !== c)) {
                selectedPiece = null;
                validMoves = [];
                renderBoard();
            }
        }
    } else {
        playSound('error');
    }
}

function handleSquareClick(r, c) {
    initAudio();
    const move = validMoves.find(m => m.r === r && m.c === c);
    if (!move) return;
    
    const pieceVal = board[selectedPiece.r][selectedPiece.c];
    let isKing = pieceVal === 3 || pieceVal === 4;
    
    // Execute move
    board[r][c] = pieceVal;
    board[selectedPiece.r][selectedPiece.c] = 0;
    
    if (move.isJump) {
        board[move.jumpedRow][move.jumpedCol] = 0;
        playSound('jump');
        
        // Add visual boom effect
        const boom = document.createElement('div');
        boom.style.position = 'absolute';
        boom.style.width = '100%';
        boom.style.height = '100%';
        boom.style.background = 'radial-gradient(circle, #fff 0%, transparent 70%)';
        boom.style.borderRadius = '50%';
        boom.style.zIndex = '50';
        boom.style.animation = 'boomAnim 0.4s ease-out forwards';
        
        const styleSheet = document.createElement('style');
        styleSheet.innerText = '@keyframes boomAnim { 0% { transform: scale(0.5); opacity: 1; } 100% { transform: scale(2); opacity: 0; } }';
        document.head.appendChild(styleSheet);
        
        const targetSquare = document.querySelector(`.square[data-r="${move.jumpedRow}"][data-c="${move.jumpedCol}"]`);
        if (targetSquare) targetSquare.appendChild(boom);
        
        setTimeout(() => { styleSheet.remove(); if(boom.parentNode) boom.remove(); }, 400);

        if (currentPlayer === 1) piecesP2--;
        else piecesP1--;
    } else {
        playSound('move');
    }
    
    // Check Crown (Virar Dama)
    let crowned = false;
    if (!isKing) {
        if (currentPlayer === 1 && r === 0) {
            board[r][c] = 3;
            crowned = true;
            setTimeout(() => playSound('king'), 200); // Slight delay after move sound
        } else if (currentPlayer === 2 && r === 7) {
            board[r][c] = 4;
            crowned = true;
            setTimeout(() => playSound('king'), 200);
        }
    }
    
    // Multi-jump logic: Se fez uma captura e NÃO virou dama agora, verifica se há nova captura
    if (move.isJump && !crowned) {
        const nextMoves = getPieceMoves(r, c, board[r][c]).filter(m => m.isJump);
        if (nextMoves.length > 0) {
            // Continua o turno da mesma peça, forçando ela a pular de novo
            selectedPiece = { r, c };
            validMoves = nextMoves.map(m => ({ ...m, fromR: r, fromC: c }));
            renderBoard();
            updateUI();
            checkWinState();
            return; 
        }
    }
    
    // Switch Turn
    currentPlayer = currentPlayer === 1 ? 2 : 1;
    selectedPiece = null;
    validMoves = [];
    
    renderBoard();
    updateUI();
    checkWinState();
    
    // Check if next player has valid moves
    const nextPlayerMoves = getValidMoves(currentPlayer);
    if (nextPlayerMoves.allMoves.length === 0 && piecesP1 > 0 && piecesP2 > 0) {
        // Player sem movimentos perde
        endGame(currentPlayer === 1 ? 2 : 1);
    }
}

function checkWinState() {
    if (piecesP1 === 0) endGame(2);
    else if (piecesP2 === 0) endGame(1);
}

function endGame(winner) {
    winnerText.textContent = winner === 1 ? 'CYBER VENCEU!' : 'NEON VENCEU!';
    winnerText.style.color = winner === 1 ? 'var(--cyan)' : 'var(--magenta)';
    winnerText.style.textShadow = winner === 1 ? 'var(--cyan-shadow)' : 'var(--magenta-shadow)';
    gameOverModal.classList.remove('hidden');
    playSound('king'); // victory sound fanfare equivalent
    setTimeout(() => playSound('king'), 200);
    setTimeout(() => playSound('king'), 400);
}

// UI Triggers
restartBtn.addEventListener('click', () => {
    playSound('move');
    initGame();
});

// Initialize audio context on first user interaction anywhere
document.addEventListener('click', initAudio, { once: true });

// Start game
initGame();
