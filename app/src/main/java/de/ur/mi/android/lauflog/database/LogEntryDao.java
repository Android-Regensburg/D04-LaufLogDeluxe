package de.ur.mi.android.lauflog.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import de.ur.mi.android.lauflog.log.LogEntry;

@Dao
public interface LogEntryDao {

    @Query("SELECT * FROM LogEntry")
    List<LogEntry> getAll();

    @Insert
    void insertAll(LogEntry... entries);

    @Delete
    void delete(LogEntry entry);

}
