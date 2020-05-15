package de.ur.mi.android.lauflog.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.ur.mi.android.lauflog.log.LogEntry;

/**
 * Vorlage für die Datenbank zur Speicherung der LogEntry-Einträge
 */
@Database(entities = {LogEntry.class}, version = 1)
@TypeConverters(LogEntryConverters.class)
public abstract class LogEntryDatabase extends RoomDatabase {
    // Methode für Zugriff auf die "Tabelle" mit den LogEntry-Einrägen
    public abstract LogEntryDao entries();
}
