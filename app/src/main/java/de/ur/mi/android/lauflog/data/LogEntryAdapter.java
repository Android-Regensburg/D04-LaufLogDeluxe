package de.ur.mi.android.lauflog.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import de.ur.mi.android.lauflog.R;

public class LogEntryAdapter extends ArrayAdapter<LogEntry> {

    private ArrayList<LogEntry> entries;

    public LogEntryAdapter(Context context, ArrayList<LogEntry> entries) {
        super(context, R.layout.item_log_entry, entries);
        this.entries = entries;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View entryView = convertView;
        if(entryView == null) {
            entryView =  LayoutInflater.from(this.getContext()).inflate(R.layout.item_log_entry, null);
        }
        LogEntry currentEntry = entries.get(position);
        if(currentEntry != null) {
            TextView dateView = entryView.findViewById(R.id.log_entry_date);
            TextView distanceView = entryView.findViewById(R.id.log_entry_distance);
            TextView timeView = entryView.findViewById(R.id.log_entry_time);
            TextView paceView = entryView.findViewById(R.id.log_entry_pace);
            dateView.setText(getFormattedDate(currentEntry.getDate()));
            distanceView.setText(getFormattedDistance(currentEntry.getDistance(), "km"));
            timeView.setText(getFormatedTime(currentEntry.getTime(), "min"));
            paceView.setText(getFormatedTime(currentEntry.getPace(), ""));
        }
        return entryView;
    }

    private String getFormattedDate(LocalDateTime date) {
       return DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.getDefault()).format(date);
    }

    private String getFormattedDistance(float distance, String suffix) {
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(distance) + suffix;
    }

    private String getFormatedTime(float time, String suffix) {
        int minutes = (int) time;
        int seconds = (int) ((time - minutes) * 60);
        String minutesString = minutes > 10 ? "" + minutes : "0" + minutes;
        String secondsString = seconds > 10 ? "" + seconds : "0" + seconds;
        return minutesString + ":" + secondsString + suffix;
    }

}
