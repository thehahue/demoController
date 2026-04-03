# Design: Wordl Übersichtsgrafik für Schüler

**Datum:** 2026-04-03
**Zielgruppe:** Fortgeschrittene Schüler (kennen HTTP, REST, haben bereits programmiert)
**Format:** Eigenständige interaktive HTML-Seite (kein Build-Tool, direkt im Browser öffenbar)

---

## Ziel

Eine einzige HTML-Datei (`docs/overview.html`), die das Wordl-Projekt für fortgeschrittene Schüler verständlich erklärt. Zwei Abschnitte: zuerst das Systemdiagramm als Überblick, dann ein animierter Request-Flow als konkretes Beispiel.

---

## Abschnitt 1: Systemarchitektur

Zeigt die zwei laufenden Spring-Boot-Prozesse und ihre Verbindung.

**Inhalt:**
- Zwei Service-Boxen: Browser/Frontend (Port 8080) und Backend (Port 8081)
- HTTP-Verbindung mit hohem Kontrast: Request-Pfeil (orange) mit `POST /api/games/{id}/guesses`, Response-Pfeil (blau) mit `200 OK · GameStateResponse`
- Label: "HTTP fetch() · JSON · CORS"
- Backend-Schichten: `GameController` → `GameService` → Domain-Klassen (`GameSession`, `GuessEvaluation`, `LetterState`)
- Kurze Erklärung warum CORS nötig ist (zwei separate Prozesse auf verschiedenen Ports)
- Package-Struktur: `api/`, `service/`, `game/`, `config/`

---

## Abschnitt 2: Animierter Request-Flow

Verfolgt einen konkreten Spielzug durch alle Schichten. Schritte werden per Klick auf "Weiter" einzeln aufgedeckt.

**Schritte:**
1. Spieler tippt "STEIN" und drückt ENTER
2. `app.js`: `handleInput("ENTER")` → `submitGuess()` → `fetch("/api/games/{id}/guesses", {method:"POST", body: JSON})`
3. HTTP-Request trifft Backend: `GameController.submitGuess(@PathVariable UUID, @RequestBody GuessRequest)`
4. `GameService.evaluateGuess()` berechnet für jeden Buchstaben: `CORRECT` / `PRESENT` / `ABSENT`
5. `GameService` gibt `GameStateResponse` zurück → Controller serialisiert zu JSON
6. Browser empfängt JSON → `renderBoard()` malt farbige Kacheln

Jeder Schritt zeigt den echten Code-Ausschnitt aus der jeweiligen Datei plus eine kurze Erklärung in einem Satz.

---

## Technische Umsetzung

- **Eine Datei:** `docs/overview.html` — kein externes CSS, kein JS-Framework
- **Inline CSS:** Dark-Theme, konsistent mit dem Wordl-Spiel selbst
- **Animation:** Vanilla JS, Schritte per Button-Klick aufdecken (`step-by-step`)
- **Keine Abhängigkeiten:** Datei kann offline geöffnet werden

---

## Nicht in Scope

- Mehrsprachigkeit
- Tests für die HTML-Seite
- Erklärungen zu Maven/Build-System
- Erklärung der Wortlisten (`WordCatalog`)
