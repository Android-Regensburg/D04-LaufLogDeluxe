package de.ur.mi.android.lauflog.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import de.ur.mi.android.lauflog.log.LogEntry;

/**
 * Data Access Object-Interface für die Verwendung der LogEntry-Klasse mit der Room-Datenbank. Hier
 * werden Queries definiert, die später für den lesenden und schreibenden Zugriff auf die Datenbank
 * werden.
 */
@Dao
public interface LogEntryDao {

    // Gibt alle Einträge der Datenbank zurück. Der Tabellenname in der Query ergibt sich aus dem
    // entsprechenden Klassennamen.
    @Query("SELECT * FROM LogEntry")
    List<LogEntry> getAll();

    // Fügt alle übergebenen LogEntry-Objekte zur Datenbank hinzu
    @Insert
    void insertAll(LogEntry... entries);

    // Löscht das übergebene LogEntry-Objekt aus der Datenbank
    @Delete
    void delete(LogEntry entry);

}
