package de.ur.mi.android.lauflog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.ur.mi.android.lauflog.data.LogEntry;
import de.ur.mi.android.lauflog.data.LogEntryAdapter;
import de.ur.mi.android.lauflog.data.SortMode;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_NEW_LOG_ENTRY = 10001;
    public static final int RESULT_CODE_FOR_VALID_LOG_ENTRY = 10002;

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
        sortLog();
    }

    private void requestNewLogEntry() {
        Intent intent = new Intent(MainActivity.this, InputActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FOR_NEW_LOG_ENTRY);
    }

    private void addLogFromIntent(Intent data) {
        Date date = new Date(data.getExtras().getLong(InputActivity.DATE_KEY));
        float distance = data.getExtras().getFloat(InputActivity.DISTANCE_KEY);
        int minutes = data.getExtras().getInt(InputActivity.MINUTES_KEY);
        int seconds = data.getExtras().getInt(InputActivity.SECONDS_KEY);
        LogEntry entry = new LogEntry(date, minutes + seconds / 60f, distance);
        log.add(entry);
        sortLog();
    }

    private void sortLog() {
        Collections.sort(log, currentComparator);
        logAdapter.updateDataSet(new ArrayList<LogEntry>(log));
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
            case REQUEST_CODE_FOR_NEW_LOG_ENTRY:
                if (resultCode == RESULT_CODE_FOR_VALID_LOG_ENTRY) {
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
