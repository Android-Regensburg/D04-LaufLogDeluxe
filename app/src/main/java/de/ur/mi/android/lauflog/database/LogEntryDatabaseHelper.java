package de.ur.mi.android.lauflog.database;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.ur.mi.android.lauflog.log.LogEntry;

/**
 * Hilfsklasse, die den direkten Zugriff auf die Datenbank, bzw. das Room-Interface vor dem
 * Rest der Anwendung verbirgt. Die MainActivity kommuniziert mit einer Instanz dieser Klasse.
 * Nur der LogEntryDatabaseHelper greift direkt auf die Datenbank zu.
 */
public class LogEntryDatabaseHelper {
    /**
     * Name der Datenbank
     */
    private static final String DATABASE_NAME = "log-entry-database";
    private LogEntryDatabase db;

    public LogEntryDatabaseHelper(Context context) {
        // Erstellen der Datenbank bzw. falls Datenbank schon vorhanden ist, Laden der Datenbank
        db = Room.databaseBuilder(context, LogEntryDatabase.class, DATABASE_NAME).build();
    }

    /**
     * Gibt eine Liste aller LogEntry-Objekte zurück, die aktuell in der Datenbank gespeichert sind.
     */
    public ArrayList<LogEntry> getAllEntries() throws ExecutionException, InterruptedException, TimeoutException {
        // Der Zugriff auf die Datenbank darf nicht im Haupt-Thread der Anwendung erfolgen. Um
        // die Operationen auf der Datenbank in einem separaten Thread durchzuführen werden die
        // Lese- und Schreibprozesse in entsprechenden Klassen gekapselt und anschließend ausgeführt.

        // Erstellt einen ausführbaren Task (Callable) der die aktuell in der Datenbank gespeicherten
        // Einträge auslesen kann.
        GetAllLogEntriesTask task = new GetAllLogEntriesTask(db);
        // Führt den erstellten Task in einem separaten Thread aus und speichert die Ergebnisse,
        // sobald die Operation abgeschlossen ist ab.
        Future<ArrayList<LogEntry>> future = Executors.newSingleThreadExecutor().submit(task);
        // Gibt die Ergebnisse des Task (hier: Liste der Einträge aus der Datenbank) zurück.
        // Achtung: get blockiert die Ausführung des aktuellen Treads, bis der Task abgeschlossen ist.
        // Für eine produktive Lösung sollten wir die Arbeit in der Datenbank vollständig vom Rest
        // der Anwendung entkoppeln, z.B. durch eine Listener-Struktur bzw. einen Callback, der die
        // aufrufende Stelle der Anwendung informiert, sobald die Daten vorliegen. Eine einfachere,
        // aber weniger gute Lösung ist, beim Warten auf die Antwort einen maximalen Timeout zu
        // definieren, nachdem der Task abgebrochen wird. Dieser Ansatz wird hier verfolgt.
        return future.get(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Speichert das übergebenen LogEntry-Objekt dauerhaft in der Datenbank
     */
    public void storeLogEntry(LogEntry entry) {
        StoreLogEntryTask task = new StoreLogEntryTask(db, entry);
        Executors.newSingleThreadExecutor().submit(task);
    }

    /**
     * Task zum Auslesen der Datenbank-Inhalte über einen separaten Thread. Da Ergebnisse erwartet
     * werden, wird der Task als Callable erstellt.
     */
    private class GetAllLogEntriesTask implements Callable<ArrayList<LogEntry>> {
        private LogEntryDatabase db;

        public GetAllLogEntriesTask(LogEntryDatabase db) {
            this.db = db;
        }

        @Override
        public ArrayList<LogEntry> call() throws Exception {
            return (ArrayList<LogEntry>) db.entries().getAll();
        }
    }

    /**
     * Task zum HInzufügen neuer Einträge in die Datenbank-Inhalte über einen separaten Thread. Der
     * Task muss keine Ergebnisse zurückliefern und wird daher als einfaches Runnable erstellt.
     */
    private class StoreLogEntryTask implements Runnable {
        private LogEntryDatabase db;
        private LogEntry entry;

        public StoreLogEntryTask(LogEntryDatabase db, LogEntry entry) {
            this.db = db;
            this.entry = entry;
        }

        @Override
        public void run() {
            db.entries().insertAll(entry);
        }
    }
}
