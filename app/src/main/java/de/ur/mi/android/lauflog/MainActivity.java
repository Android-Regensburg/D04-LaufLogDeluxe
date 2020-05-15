package de.ur.mi.android.lauflog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import de.ur.mi.android.lauflog.database.LogEntryDatabaseHelper;
import de.ur.mi.android.lauflog.log.LogEntry;
import de.ur.mi.android.lauflog.log.LogEntryAdapter;
import de.ur.mi.android.lauflog.log.LogRequestConfig;
import de.ur.mi.android.lauflog.log.LogSortMode;

/**
 * Zentrale Activity der Anwendung, die die folgenden Aufgaben erledigt:
 * - Verwaltet eine Liste aller vorhandenen Einträge (Läufe) des Logs
 * - Ermöglicht die Eingabe neuer Einträge durch den Aufruf der InputActivity
 * - Stellt den aktuellen Inhalt der Liste in einem RecyclerView im UI dar
 *
 * Die Anwendung nutzt eine Datenbank:
 * - Für den laufenden "Betrieb" der Anwendung wird weiterhin eine ArrayList zum Speichern der
 *   Inhalte verwendet: Adapter und RecyclerView nutzten diese Liste
 * - Jeder neue Eintrag wird in der Liste und zusätzlich in der Datenbank gespeichert
 * - Beim Start der Anwendung werden alle in der Datenbank gespeicherten Einträge ausgelesen und
 *   in der ArrayList gespeichert
 */
public class MainActivity extends AppCompatActivity {

    private LogEntryDatabaseHelper dbHelper;
    private ArrayList<LogEntry> log;
    private LogEntryAdapter logAdapter;
    private RecyclerView entriesList;
    private TextView sortModeStatus;
    private LogSortMode currentSortMode = LogSortMode.DATE;
    private Comparator<LogEntry> currentComparator = LogEntry.DATE_COMPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initLog();
        initDB();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        entriesList = findViewById(R.id.log_entry_list);
        sortModeStatus = findViewById(R.id.sort_mode_status);
        sortModeStatus.setText(R.string.sort_mode_date);
    }

    private void initLog() {
        log = new ArrayList();
        logAdapter = new LogEntryAdapter(log, this);
        entriesList.setAdapter(logAdapter);
    }

    /**
     * Erstellt bzw. öffnet die Datenbank über die Helper-Klasse
     */
    private void initDB() {
        dbHelper = new LogEntryDatabaseHelper(this);
        try {
            // Versucht alle Einträge aus der Datenbank auszulesen, in der ArrayList zu speichern,
            // sortiert anschließend die Einträge und speichert die neue Liste im Adapter
            log = dbHelper.getAllEntries();
            sortAndUpdateList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sortiert die Einträge der ArrayList auf Basis des aktuell ausgewählten Sortiermodus und
     * übergibt die neue Liste im Anschluss dand en Adapter. Dieser informiert das angeschlossene
     * View über die Änderungen am Datenbestand.
     */
    private void sortAndUpdateList() {
        Collections.sort(log, currentComparator);
        logAdapter.setEntries(log);
    }

    /**
     * Ändert den aktuellen Modus zur Sortierung der Liste. Die Modi werden auf Basis der Sortierung
     * im Enum umgeschaltet. Ist aktuell der letzte Modus ausgewählt, wird beim Aufruf der Methode
     * der erste Eintrag des Enum als neuer Sortiermodus verwendet. Der ausgewählte Modus wird im
     * User Interface angezeigt und zur Sortierung der Einträge im Adapter verwendet.
     */
    private void toggleSortMode() {
        int currentModeOrdinal = currentSortMode.ordinal();
        int nextModeOrdinal = currentModeOrdinal + 1;
        if (nextModeOrdinal >= LogSortMode.values().length) {
            nextModeOrdinal = 0;
        }
        currentSortMode = LogSortMode.values()[nextModeOrdinal];
        switch (currentSortMode) {
            case DATE:
                currentComparator = LogEntry.DATE_COMPARATOR;
                sortModeStatus.setText(R.string.sort_mode_date);
                break;
            case DISTANCE:
                currentComparator = LogEntry.DISTANCE_COMPARATOR;
                sortModeStatus.setText(R.string.sort_mode_distance);
                break;
            case PACE:
                currentComparator = LogEntry.PACE_COMPARATOR;
                sortModeStatus.setText(R.string.sort_mode_pace);
                break;
        }
        sortAndUpdateList();
    }

    /**
     * Startet die InputActivity um die Daten zu einem neuen Lauf von dem Nutzer bzw. von der Nutzerin
     * anzufordern.
     */
    private void requestNewLogEntry() {
        Intent intent = new Intent(MainActivity.this, InputActivity.class);
        startActivityForResult(intent, LogRequestConfig.REQUEST_CODE_FOR_NEW_LOG_ENTRY);
    }

    /**
     * Verwendet die als Ergebnis der InputActivity zurückgegebenen Informationen zum Erstellen
     * eines neuen Eintrags in der Liste der gespeicherten Läufe. Im Anschluss werden die im Adapter
     * gespeicherten Werte überschrieben und neu sortiert.
     */
    private void addLogFromIntent(Intent data) {
        Date date = new Date(data.getExtras().getLong(LogRequestConfig.DATE_KEY));
        float distance = data.getExtras().getFloat(LogRequestConfig.DISTANCE_KEY);
        int minutes = data.getExtras().getInt(LogRequestConfig.MINUTES_KEY);
        int seconds = data.getExtras().getInt(LogRequestConfig.SECONDS_KEY);
        LogEntry entry = new LogEntry(date, minutes + seconds / 60f, distance);
        // Speichert den neuen Eintrag in der flüchtigen ArrayList
        log.add(entry);
        // Speichert den Eintrag zusätzlich in der persistenten Datenbank
        dbHelper.storeLogEntry(entry);
        sortAndUpdateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Callback-Methode, die aufgerufen wird, wenn eine Activity, die per startActivityForResult
     * gestartet wurde, geschlossen wird und Ergebnisse zurückliefert.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LogRequestConfig.REQUEST_CODE_FOR_NEW_LOG_ENTRY:
                if (resultCode == Activity.RESULT_OK) {
                    addLogFromIntent(data);
                }
        }
    }

    /**
     * Callback-Methode, die aufgerufen wird, wenn einer der Einträge im Menü (App bar) von den
     * Nutzern und NutzerInnen ausgewählt wird.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_log_entry:
                requestNewLogEntry();
                break;
            case R.id.toggle_sort_mode:
                toggleSortMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
