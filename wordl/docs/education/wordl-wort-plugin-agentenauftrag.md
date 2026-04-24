# Agentenauftrag: WORDL Wortlisten-Plugin erstellen

Diese Anleitung ist fuer einen Agenten gedacht, der spaeter ein externes WORDL-Wortlisten-Plugin als JAR erstellen soll. Im WORDL-Hauptprojekt selbst soll dabei nichts geaendert werden.

## Ziel

Erstelle ein separates Java-JAR, das eine Wortliste fuer genau eine Wortlaenge bereitstellt. Der Plugin-Typ ist die Anzahl der Buchstaben.

Beispiel:

- Sieben-Buchstaben-Woerter: `type()` gibt `7` zurueck.
- Acht-Buchstaben-Woerter: `type()` gibt `8` zurueck.

## Vertrag

Das Plugin muss dieses Interface implementieren:

```java
de.demo.wordl.backend.plugin.WordListPlugin
```

Das Interface hat zwei Methoden:

```java
int type();

List<String> words();
```

Anforderungen:

- `type()` muss exakt der Wortlaenge entsprechen.
- `words()` muss mindestens ein gueltiges Wort liefern.
- Jedes Wort muss nur aus `A` bis `Z` bestehen.
- Jedes Wort muss exakt `type()` Zeichen lang sein.
- Verwende keine Umlaute, Leerzeichen, Bindestriche oder Sonderzeichen.

## JAR-Struktur

Das fertige JAR muss mindestens diese Inhalte haben:

```text
mein-wort-plugin.jar
  com/example/wordl/SevenLetterWordsPlugin.class
  META-INF/services/de.demo.wordl.backend.plugin.WordListPlugin
```

Die Service-Datei enthaelt genau den vollqualifizierten Klassennamen:

```text
com.example.wordl.SevenLetterWordsPlugin
```

## Maven-Hinweis

Das Plugin-Projekt braucht beim Kompilieren Zugriff auf das WORDL-Backend-JAR, damit das Interface `WordListPlugin` bekannt ist. Das Backend-JAR soll dabei nur als Compile-Abhaengigkeit verwendet werden. Das Plugin-JAR selbst muss das Backend nicht einpacken.

Geeignete Dependency-Einstellung:

```xml
<dependency>
    <groupId>de.demo.wordl</groupId>
    <artifactId>backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Wenn das Backend-JAR nicht in einem Maven-Repository liegt, installiere es vorher lokal oder referenziere es im Plugin-Build auf eine projektspezifische Weise.

## Arbeitsschritte fuer den Agenten

1. Lege ein separates Plugin-Projekt ausserhalb des WORDL-Hauptprojekts an.
2. Fuege die Compile-Abhaengigkeit auf das WORDL-Backend hinzu.
3. Erstelle eine Klasse, die `WordListPlugin` implementiert.
4. Gib in `type()` die Wortlaenge zurueck.
5. Gib in `words()` die Wortliste zurueck.
6. Erstelle `src/main/resources/META-INF/services/de.demo.wordl.backend.plugin.WordListPlugin`.
7. Schreibe den vollqualifizierten Implementierungsnamen in diese Service-Datei.
8. Baue das Plugin als JAR.
9. Lege das JAR in denselben Ordner wie das gestartete WORDL-Spiel-JAR.
10. Starte WORDL neu und pruefe `GET /api/games/word-lengths`.

## Abnahmekriterien

Ein Plugin ist korrekt, wenn:

- Das JAR ohne Aenderungen am WORDL-Hauptprojekt erstellt wurde.
- Das JAR einen gueltigen `META-INF/services`-Eintrag enthaelt.
- WORDL nach Neustart die neue Wortlaenge in `/api/games/word-lengths` anzeigt.
- Ein Spiel mit dieser Wortlaenge per `POST /api/games` gestartet werden kann.
- Die geratenen Woerter im Spiel genau die neue Laenge verwenden.

