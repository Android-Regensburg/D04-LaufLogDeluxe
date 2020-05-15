package de.ur.mi.android.lauflog.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.ur.mi.android.lauflog.log.LogEntry;

@Database(entities = {LogEntry.class}, version = 1)
@TypeConverters(LogEntryConverters.class)
public abstract class LogEntryDatabase extends RoomDatabase {
    public abstract LogEntryDao entryDao();
}
