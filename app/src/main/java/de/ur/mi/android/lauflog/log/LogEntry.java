package de.ur.mi.android.lauflog.log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Date;

/**
 * Klasse für immutable Instanzen zur Abbildung einzelner Einträge im LaufLog. Zeitpunkt, Dauer und
 * Geschwindigkeit werden im Konstruktor übergeben und können nicht verwändert werden. Alle
 * Eigenschaften sind über Getter-Methoden zugänglich. Zum Vergleich bzw. zur Sortierung mehrere
 * LogEntry-Objekte anhand unterschiedlicher Kriterien bietet die Klasse verschiedene Comparator,
 * z.B. für die Verwendung mit Collections.sort an.
 *
 * Instanzen der LogEntry-Klassen können in einer Room-Datenbank persitiert werden. Dafür werden
 * die einzelnen Felder der Klasse über entsprechende Annotationen konkreten Spalten in der automatisch
 * erstellten LogEntry-Tabelle der Datenbank zugeordnet.
 */
@Entity
public class LogEntry {
    // Annotation für Room-Datenbank: Automatisch erzeugter Primärschlüssel
    @PrimaryKey(autoGenerate = true)
    public Integer uid;
    // Annotation für Room-Datenbank: Spaltennamen für Datums-Eigenschaft
    // Instanzen komplexer Datentypen, wie hier die Date-Klasse, können nicht automatisch für
    // die Speicherung in der Room-Datenbank konvertiert werden. Daher ist das Bereitstellen
    // von Konvertern für diese Datentypen notwendig (siehe LogEntryConverters).
    @ColumnInfo(name = "date")
    public final Date date;
    // Annotation für Room-Datenbank: Spaltenname für Zeit-Eigenschaft
    @ColumnInfo(name = "time_in_minutes")
    public final float timeInMinutes;
    // Annotation für Room-Datenbank: Spaltenname für Strecken-Eigenschaft
    @ColumnInfo(name = "distance_in_kilomenters")
    public final float distanceInKilometers;

    /**
     * Vorgefertigter Comparator, mit dessen Hilfe LogEntry-Objekte anhand des gespeicherten
     * Datums mit anderen LogEntry-Objekten verglichen werden können. Aktuellere Läufe werden vor
     * älteren  einsortiert.
     */
    public static final Comparator<LogEntry> DATE_COMPARATOR = new Comparator<LogEntry>() {
        @Override
        public int compare(LogEntry o1, LogEntry o2) {
            long thisDateInMs = o1.date.getTime();
            long thatDateInMs = o2.date.getTime();
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
            float distanceDelta = o1.distanceInKilometers - o2.distanceInKilometers;
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

    public float getPace() {
        return timeInMinutes / distanceInKilometers;
    }
}
