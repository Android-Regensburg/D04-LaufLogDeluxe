package de.ur.mi.android.lauflog.database;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.ur.mi.android.lauflog.log.LogEntry;

public class LogEntryDatabaseHelper {

    private static final String DATABASE_NAME = "log-entry-database";
    private LogEntryDatabase db;

    public LogEntryDatabaseHelper(Context context) {
        db = Room.databaseBuilder(context, LogEntryDatabase.class, DATABASE_NAME).build();
    }

    public ArrayList<LogEntry> getAllEntries() throws ExecutionException, InterruptedException {
        GetAllLogEntriesTask task = new GetAllLogEntriesTask(db);
        Future<ArrayList<LogEntry>> future = Executors.newSingleThreadExecutor().submit(task);
        return future.get();
    }

    public void storeLogEntry(LogEntry entry) {
        StoreLogEntryTask task = new StoreLogEntryTask(db, entry);
        Executors.newSingleThreadExecutor().submit(task);
    }

    private class GetAllLogEntriesTask implements Callable<ArrayList<LogEntry>> {
        private LogEntryDatabase db;

        public GetAllLogEntriesTask(LogEntryDatabase db) {
            this.db = db;
        }

        @Override
        public ArrayList<LogEntry> call() throws Exception {
            return (ArrayList<LogEntry>) db.entryDao().getAll();
        }
    }

    private class StoreLogEntryTask implements Runnable {
        private LogEntryDatabase db;
        private LogEntry entry;

        public StoreLogEntryTask(LogEntryDatabase db, LogEntry entry) {
            this.db = db;
            this.entry = entry;
        }

        @Override
        public void run() {
            db.entryDao().insertAll(entry);
        }
    }
}
