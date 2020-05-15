package de.ur.mi.android.lauflog.log;

import java.util.Comparator;
import java.util.Date;

/**
 * Klasse für immutable Instanzen zur Abbildung einzelner Einträge im LaufLog. Zeitpunkt, Dauer und
 * Geschwindigkeit werden im Konstruktor übergeben und können nicht verwändert werden. Alle
 * Eigenschaften sind über Getter-Methoden zugänglich. Zum Vergleich bzw. zur Sortierung mehrere
 * LogEntry-Objekte anhand unterschiedlicher Kriterien bietet die Klasse verschiedene Comparator,
 * z.B. für die Verwendung mit Collections.sort an.
 */
public class LogEntry {

    private final Date date;
    private final float timeInMinutes;
    private final float distanceInKilometers;

    /**
     * Vorgefertigter Comparator, mit dessen Hilfe LogEntry-Objekte anhand des gespeicherten
     * Datums mit anderen LogEntry-Objekten verglichen werden können. Aktuellere Läufe werden vor
     * älteren  einsortiert.
     */
    public static final Comparator<LogEntry> DATE_COMPARATOR = new Comparator<LogEntry>() {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            long thisDateInMs = o1.getDate().getTime();
            long thatDateInMs = o2.getDate().getTime();
            return (int) (thisDateInMs - thatDateInMs);
        }
    };

    /**
     * Vorgefertigter Comparator, mit dessen Hilfe LogEntry-Objekte anhand der gelaufenen Distanz
     * mit anderen LogEntry-Objekten verglichen werden können. Längeren Läuf werden vor kürzeren
     * einsortiert.
     */
    public static final Comparator<LogEntry> DISTANCE_COMPARATOR = new Comparator<LogEntry>() {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            float distanceDelta = o1.getDistance() - o2.getDistance();
            return distanceDelta > 0 ? -1 : 1;
        }
    };

    /**
     * Vorgefertigter Comparator, mit dessen Hilfe LogEntry-Objekte anhand der gespeicherten
     * Geschwindigkeit mit anderen LogEntry-Objekten verglichen werden können. Schnellere Läufe
     * werden vor langsameren einsortiert.
     */
    public static final Comparator<LogEntry> PACE_COMPARATOR = new Comparator<LogEntry>() {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            float paceDelta = o1.getPace() - o2.getPace();
            return paceDelta > 0 ? 1 : -1;
        }
    };


    public LogEntry(Date date, float timeInMinutes, float distanceInKilometers) {
        this.date = date;
        this.timeInMinutes = timeInMinutes;
        this.distanceInKilometers = distanceInKilometers;
    }

    public Date getDate() {
        return date;
    }

    public float getTime() {
        return timeInMinutes;
    }

    public float getDistance() {
        return distanceInKilometers;
    }

    public float getPace() {
        return timeInMinutes / distanceInKilometers;
    }
}
