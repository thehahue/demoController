package at.bbrz.demo.wetten;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum ErgebnisMuster {
    ERGEBNISSTAND("X:X"),
    ERGEBNISSTAND_NACH_VERLAENGERUNG("X:X n.V."),
    ERGEBNISSTAND_NACH_PENALTY("X:X n.P."),
    SATZERGEBNIS("X:X Saetze"),
    SATZDETAIL("X:X, X:X, X:X"),
    KO_RUNDE("KO Runde X"),
    TKO_RUNDE("TKO Runde X"),
    PUNKTSIEG("Punktsieg");

    private final String anzeige;

    ErgebnisMuster(String anzeige) {
        this.anzeige = anzeige;
    }

    @JsonValue
    public String getAnzeige() {
        return anzeige;
    }

    @JsonCreator
    public static ErgebnisMuster fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalizedValue = normalize(value);

        for (ErgebnisMuster muster : values()) {
            if (normalizedValue.equals(normalize(muster.name()))
                    || normalizedValue.equals(normalize(muster.anzeige))) {
                return muster;
            }
        }

        if (normalizedValue.matches("\\d+:\\d+")) {
            return ERGEBNISSTAND;
        }
        if (normalizedValue.matches("\\d+:\\d+ n\\.v\\.")) {
            return ERGEBNISSTAND_NACH_VERLAENGERUNG;
        }
        if (normalizedValue.matches("\\d+:\\d+ n\\.p\\.")) {
            return ERGEBNISSTAND_NACH_PENALTY;
        }
        if (normalizedValue.matches("\\d+:\\d+ saetze")) {
            return SATZERGEBNIS;
        }
        if (normalizedValue.matches("\\d+:\\d+(,\\s*\\d+:\\d+){2,}")) {
            return SATZDETAIL;
        }
        if (normalizedValue.matches("ko runde (x|\\d+)")) {
            return KO_RUNDE;
        }
        if (normalizedValue.matches("tko runde (x|\\d+)")) {
            return TKO_RUNDE;
        }
        if (normalizedValue.equals("punktsieg")) {
            return PUNKTSIEG;
        }

        throw new IllegalArgumentException("Unbekanntes Ergebnismuster: " + value);
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
