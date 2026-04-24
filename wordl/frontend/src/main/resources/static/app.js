const API_BASE = `${window.location.protocol}//${window.location.hostname || "localhost"}:8081/api`;
const MAX_ATTEMPTS = 6;
const KEYBOARD_LAYOUT = [
    ["Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"],
    ["A", "S", "D", "F", "G", "H", "J", "K", "L"],
    ["ENTER", "Z", "X", "C", "V", "B", "N", "M", "BACK"]
];
const STATE_PRIORITY = {
    ABSENT: 1,
    PRESENT: 2,
    CORRECT: 3
};

const elements = {
    board: document.getElementById("board"),
    gameMeta: document.getElementById("gameMeta"),
    guessForm: document.getElementById("guessForm"),
    guessInput: document.getElementById("guessInput"),
    keyboard: document.getElementById("keyboard"),
    newGameButton: document.getElementById("newGameButton"),
    sizePicker: document.getElementById("sizePicker"),
    statusMessage: document.getElementById("statusMessage"),
    statusPill: document.getElementById("statusPill"),
    submitGuessButton: document.getElementById("submitGuessButton")
};

const state = {
    selectedWordLength: 5,
    supportedWordLengths: [4, 5, 6],
    currentGuess: "",
    game: null,
    busy: false
};

function setBusy(isBusy) {
    state.busy = isBusy;
    elements.newGameButton.disabled = isBusy;
    elements.submitGuessButton.disabled = isBusy;
}

function setStatus(message, type = "info") {
    elements.statusMessage.textContent = message;
    elements.statusMessage.className = "status-message";
    if (type === "error") {
        elements.statusMessage.classList.add("status-message--error");
    }
    if (type === "success") {
        elements.statusMessage.classList.add("status-message--success");
    }
}

function setStatusPill() {
    const pill = elements.statusPill;
    pill.className = "pill";

    if (!state.game) {
        pill.textContent = "In Vorbereitung";
        return;
    }

    if (state.game.status === "WON") {
        pill.classList.add("pill--won");
        pill.textContent = "Gewonnen";
        return;
    }

    if (state.game.status === "LOST") {
        pill.classList.add("pill--lost");
        pill.textContent = "Verloren";
        return;
    }

    pill.textContent = `Zug ${state.game.attemptCount + 1}/${state.game.maxAttempts}`;
}

function renderSizePicker() {
    elements.sizePicker.innerHTML = "";
    state.supportedWordLengths.forEach((wordLength) => {
        const button = document.createElement("button");
        button.className = "chip";
        button.dataset.length = String(wordLength);
        button.type = "button";
        button.textContent = String(wordLength);
        elements.sizePicker.append(button);
    });

    [...elements.sizePicker.querySelectorAll("[data-length]")].forEach((button) => {
        button.classList.toggle("chip--active", Number(button.dataset.length) === state.selectedWordLength);
        button.disabled = state.busy;
    });
}

function buildKeyboardState() {
    const keyboardState = new Map();

    if (!state.game) {
        return keyboardState;
    }

    state.game.guesses.forEach((guess) => {
        guess.letters.forEach((letter) => {
            const currentPriority = STATE_PRIORITY[keyboardState.get(letter.letter)] || 0;
            const nextPriority = STATE_PRIORITY[letter.state] || 0;
            if (nextPriority > currentPriority) {
                keyboardState.set(letter.letter, letter.state);
            }
        });
    });

    return keyboardState;
}

function createTile(letter = "", tileState = "", revealed = false) {
    const tile = document.createElement("div");
    tile.className = "tile";
    if (letter) {
        tile.classList.add("tile--filled");
    }
    if (tileState) {
        tile.classList.add(`tile--${tileState.toLowerCase()}`);
    }
    if (revealed) {
        tile.classList.add("tile--revealed");
    }
    tile.textContent = letter;
    return tile;
}

function renderBoard() {
    const wordLength = state.game?.wordLength || state.selectedWordLength;
    elements.board.innerHTML = "";
    elements.board.style.gridTemplateRows = `repeat(${MAX_ATTEMPTS}, minmax(0, 1fr))`;

    for (let rowIndex = 0; rowIndex < MAX_ATTEMPTS; rowIndex += 1) {
        const row = document.createElement("div");
        row.className = "board__row";
        row.style.gridTemplateColumns = `repeat(${wordLength}, minmax(0, 4.2rem))`;

        const pastGuess = state.game?.guesses?.[rowIndex];
        const isCurrentRow = !pastGuess && rowIndex === (state.game?.attemptCount || 0);

        for (let columnIndex = 0; columnIndex < wordLength; columnIndex += 1) {
            if (pastGuess) {
                const evaluation = pastGuess.letters[columnIndex];
                row.append(createTile(evaluation.letter, evaluation.state, true));
                continue;
            }

            const letter = isCurrentRow ? state.currentGuess[columnIndex] || "" : "";
            row.append(createTile(letter));
        }

        elements.board.append(row);
    }
}

function renderKeyboard() {
    const keyboardState = buildKeyboardState();
    elements.keyboard.innerHTML = "";

    KEYBOARD_LAYOUT.forEach((rowKeys) => {
        const row = document.createElement("div");
        row.className = "keyboard__row";

        rowKeys.forEach((key) => {
            const button = document.createElement("button");
            button.type = "button";
            button.className = "keyboard__key";
            button.dataset.key = key;
            button.textContent = key === "BACK" ? "DEL" : key;
            button.disabled = state.busy;

            if (key === "ENTER" || key === "BACK") {
                button.classList.add("keyboard__key--wide");
            }

            if (keyboardState.has(key)) {
                button.dataset.state = keyboardState.get(key);
            }

            button.addEventListener("click", () => handleInput(key));
            row.append(button);
        });

        elements.keyboard.append(row);
    });
}

function renderMeta() {
    if (!state.game) {
        elements.gameMeta.textContent = `Wortlänge ${state.selectedWordLength}`;
        return;
    }

    const remainingAttempts = state.game.maxAttempts - state.game.attemptCount;
    elements.gameMeta.textContent = `${state.game.wordLength} Buchstaben, ${remainingAttempts} Versuche frei`;
}

function syncInput() {
    elements.guessInput.maxLength = state.game?.wordLength || state.selectedWordLength;
    elements.guessInput.value = state.currentGuess;
    elements.guessInput.disabled = state.busy || state.game?.status !== "IN_PROGRESS";
}

function render() {
    renderSizePicker();
    setStatusPill();
    renderMeta();
    syncInput();
    renderBoard();
    renderKeyboard();
}

async function requestJson(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        let message = "Die Anfrage ist fehlgeschlagen.";
        try {
            const error = await response.json();
            if (error.message) {
                message = error.message;
            }
        } catch (ignored) {
            if (response.statusText) {
                message = response.statusText;
            }
        }
        throw new Error(message);
    }

    return response.json();
}

async function loadSupportedWordLengths() {
    try {
        const supportedWordLengths = await requestJson("/games/word-lengths");
        if (Array.isArray(supportedWordLengths) && supportedWordLengths.length > 0) {
            state.supportedWordLengths = supportedWordLengths;
            if (!state.supportedWordLengths.includes(state.selectedWordLength)) {
                state.selectedWordLength = state.supportedWordLengths[0];
            }
        }
    } catch (error) {
        setStatus(error.message, "error");
    }
}

async function startNewGame() {
    setBusy(true);
    setStatus("Neues Spiel wird geladen ...");
    state.currentGuess = "";
    render();

    try {
        state.game = await requestJson("/games", {
            method: "POST",
            body: JSON.stringify({ wordLength: state.selectedWordLength })
        });
        setStatus(`Neues ${state.game.wordLength}-Buchstaben-Spiel gestartet.`);
    } catch (error) {
        setStatus(error.message, "error");
    } finally {
        setBusy(false);
        render();
        elements.guessInput.focus();
    }
}

async function submitGuess() {
    if (!state.game || state.game.status !== "IN_PROGRESS" || state.busy) {
        return;
    }

    if (state.currentGuess.length !== state.game.wordLength) {
        setStatus(`Bitte genau ${state.game.wordLength} Buchstaben eingeben.`, "error");
        return;
    }

    setBusy(true);

    try {
        state.game = await requestJson(`/games/${state.game.gameId}/guesses`, {
            method: "POST",
            body: JSON.stringify({ guess: state.currentGuess })
        });
        state.currentGuess = "";

        if (state.game.status === "WON") {
            setStatus(state.game.message, "success");
        } else if (state.game.status === "LOST") {
            setStatus(state.game.message, "error");
        } else {
            setStatus(state.game.message);
        }
    } catch (error) {
        setStatus(error.message, "error");
    } finally {
        setBusy(false);
        render();
        elements.guessInput.focus();
    }
}

function handleInput(rawKey) {
    if (!state.game || state.game.status !== "IN_PROGRESS" || state.busy) {
        return;
    }

    const key = rawKey.toUpperCase();
    if (key === "ENTER") {
        submitGuess();
        return;
    }

    if (key === "BACK" || key === "BACKSPACE") {
        state.currentGuess = state.currentGuess.slice(0, -1);
        render();
        return;
    }

    if (!/^[A-Z]$/.test(key)) {
        return;
    }

    if (state.currentGuess.length >= state.game.wordLength) {
        return;
    }

    state.currentGuess += key;
    render();
}

elements.newGameButton.addEventListener("click", startNewGame);

elements.sizePicker.addEventListener("click", (event) => {
    const button = event.target.closest("[data-length]");
    if (!button || state.busy) {
        return;
    }
    state.selectedWordLength = Number(button.dataset.length);
    render();
    startNewGame();
});

elements.guessForm.addEventListener("submit", (event) => {
    event.preventDefault();
    submitGuess();
});

elements.guessInput.addEventListener("input", (event) => {
    const sanitized = event.target.value.toUpperCase().replace(/[^A-Z]/g, "");
    const maxLength = state.game?.wordLength || state.selectedWordLength;
    state.currentGuess = sanitized.slice(0, maxLength);
    render();
});

document.addEventListener("keydown", (event) => {
    if (event.metaKey || event.ctrlKey || event.altKey) {
        return;
    }

    if (document.activeElement === elements.guessInput) {
        if (event.key === "Enter") {
            event.preventDefault();
            submitGuess();
        }
        return;
    }

    handleInput(event.key);
});

async function initialize() {
    render();
    await loadSupportedWordLengths();
    render();
    await startNewGame();
}

initialize();
