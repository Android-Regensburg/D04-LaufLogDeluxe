package de.ur.mi.android.lauflog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import de.ur.mi.android.lauflog.request.RequestConfig;
import de.ur.mi.android.lauflog.request.LogEntry;
import de.ur.mi.android.lauflog.request.LogEntryAdapter;
import de.ur.mi.android.lauflog.request.SortMode;

public class MainActivity extends AppCompatActivity {

    private ArrayList<LogEntry> log;
    private LogEntryAdapter logAdapter;
    private ListView entriesList;
    private TextView sortModeStatus;

    private SortMode currentSortMode = SortMode.DATE;
    private Comparator<LogEntry> currentComparator = LogEntry.getDateComparator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initLog();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        entriesList = findViewById(R.id.log_entry_list);
        sortModeStatus = findViewById(R.id.sort_mode_status);
        sortModeStatus.setText(R.string.sort_mode_date);
    }

    private void initLog() {
        log = new ArrayList<>();
        logAdapter = new LogEntryAdapter(this, log);
        entriesList.setAdapter(logAdapter);
    }

    /**
     * Ändert den aktuellen Modus zur Sortierung der Liste. Die Modi werden auf Basis der Sortierung
     * im Enum umgeschaltet. Ist aktuell der letzte Modus ausgewählt, wird beim Aufruf der Methode
     * der erste Einträge des Enum als neuer Sortiermodus verwendet. Der ausgewählte Modus wird im
     * User Interface angezeigt und zur Sortierung der Einträge im Adapter verwendet.
     */
    private void toggleSortMode() {
        int currentModeOrdinal = currentSortMode.ordinal();
        int nextModeOrdinal = currentModeOrdinal + 1;
        if (nextModeOrdinal >= SortMode.values().length) {
            nextModeOrdinal = 0;
        }
        currentSortMode = SortMode.values()[nextModeOrdinal];
        switch (currentSortMode) {
            case DATE:
                currentComparator = LogEntry.getDateComparator();
                sortModeStatus.setText(R.string.sort_mode_date);
                break;
            case DISTANCE:
                currentComparator = LogEntry.getDistanceComparator();
                sortModeStatus.setText(R.string.sort_mode_distance);
                break;
            case PACE:
                currentComparator = LogEntry.getPaceComparator();
                sortModeStatus.setText(R.string.sort_mode_pace);
                break;
        }
        logAdapter.sortDataSet(currentComparator);
    }

    /**
     * Startet die InputActivity um die Daten zu einem neuen Lauf von dem Nutzer bzw. von der Nutzerin
     * anzufordern.
     */
    private void requestNewLogEntry() {
        Intent intent = new Intent(MainActivity.this, InputActivity.class);
        startActivityForResult(intent, RequestConfig.REQUEST_CODE_FOR_NEW_LOG_ENTRY);
    }

    /**
     * Verwendet die als Ergebnis der InputActivity zurückgegebenen Informationen zum Erstellen
     * eines neuen Eintrags in der Liste der gespeicherten Läufe. Im Anschluss werden die im Adapter
     * gespeicherten Werte überschrieben und neu sortiert.
     */
    private void addLogFromIntent(Intent data) {
        Date date = new Date(data.getExtras().getLong(RequestConfig.DATE_KEY));
        float distance = data.getExtras().getFloat(RequestConfig.DISTANCE_KEY);
        int minutes = data.getExtras().getInt(RequestConfig.MINUTES_KEY);
        int seconds = data.getExtras().getInt(RequestConfig.SECONDS_KEY);
        LogEntry entry = new LogEntry(date, minutes + seconds / 60f, distance);
        log.add(entry);
        // Activity und Adapter operieren auf unabhängigen Listen. Änderungen im Adapter (Sortierung
        // oder Löschen von Einträgen) wirken sich so nicht auf die hier in der Acitivity gespeicherten
        // und verwalteten Einträge aus: Adapter und ListView sind nur für die Darstellung der Einträge
        // im User Interface verantwortlich. Die eigentliche Datengrundlage der Anwendung (Liste der Einträge)
        // wird soweit wie möglich von diesem Bereich getrennt. Da es sich bei den verwendeten
        // LogEntry-Objekten um Immutables (https://en.wikipedia.org/wiki/Immutable_object) handelt
        // ist die Verwendung einer "shallow copy" im Adapter ausreichend.
        logAdapter.updateDataSet(new ArrayList<>(log));
        logAdapter.sortDataSet(currentComparator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestConfig.REQUEST_CODE_FOR_NEW_LOG_ENTRY:
                if (resultCode == Activity.RESULT_OK) {
                    addLogFromIntent(data);
                }
        }
    }

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
