package de.ur.mi.android.lauflog.request;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import de.ur.mi.android.lauflog.R;

/**
 * Adapter zur Anzeige einer Liste von LogEntries als Inhalt einer ListView im User Interface
 */
    public class LogEntryAdapter extends ArrayAdapter<LogEntry> {

    // Liste der LogEntries, die über den Adapter verwaltet werden
    private ArrayList<LogEntry> entries;

    public LogEntryAdapter(Context context, ArrayList<LogEntry> entries) {
        super(context, R.layout.item_log_entry, entries);
        this.entries = entries;
    }

    /**
     * Sortiert die aktuell im Adapter gespeicherte Liste von LogEntries anhand des übergebenen
     * Comparators und informiert anschließend das angeschlossene ListView.
     */
    public void sortDataSet(Comparator<LogEntry> comparator) {
        Collections.sort(entries, comparator);
        this.notifyDataSetChanged();
    }

    /**
     * Ersetzt die aktuell im Adapter gespeicherte Liste von LogEntries durch eine neue und
     * informiert anschließend das angeschlossene ListView
     */
    public void updateDataSet(ArrayList<LogEntry> entries) {
        Log.d("LaufApp", "Set new list (size: " + entries.size());
        this.entries = entries;
        this.notifyDataSetChanged();
    }

    /**
     * Wird vom angeschlossenen ListView aufgerufen, wenn Views für die Anzeige der einzelnen Listen-
     * elementen benötigt wird. Wann das ListView diese anfordert wird vom Android-System gesteuert, z.B.
     * beim Scrollen in der Liste.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LogEntry currentEntry = entries.get(position);
        return renderEntryInView(convertView, currentEntry);
    }

    /**
     * Gibt ein View zur Anzeige des übergebenen LogEntry in der angeschlossenen ListView zurück.
     * Wurde für die Listenposition noch kein View erstellt (convertView == null) wird ein solches
     * Element neu erstellt. In beiden Fällen werden die Eigenschaften des LogEntry-Objekts ausgelesen
     * und in den entsprechenden TextViews des ListView-Eintrags eingetragen. Vor dem Eintragen der
     * Werte im User Interface werden diese in ein jeweils passendes Format überführt.
     */
    private View renderEntryInView(View convertView, LogEntry entry) {
        View entryView = convertView;
        if (entryView == null) {
            entryView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_log_entry, null);
        }
        if (entry != null) {
            TextView dateView = entryView.findViewById(R.id.log_entry_date);
            TextView distanceView = entryView.findViewById(R.id.log_entry_distance);
            TextView timeView = entryView.findViewById(R.id.log_entry_time);
            TextView paceView = entryView.findViewById(R.id.log_entry_pace);
            dateView.setText(getFormattedDate(entry.getDate()));
            distanceView.setText(getFormattedDistance(entry.getDistance(), getContext().getString(R.string.kilometer_suffix)));
            timeView.setText(getFormattedTime(entry.getTime(), getContext().getString(R.string.minutes_suffix)));
            paceView.setText(getFormattedTime(entry.getPace()));
        }
        return entryView;
    }

    private String getFormattedDate(Date date) {
        // SimpleDateFormat erlaubt die konfigurierbare Darstellung von Date-Objekten in einem
        // menschenlesbaren Format. Im übergebenen Pattern können Position und Darstellung der
        // einzelnen Teile (z.B. Tag oder Jahr) durch entsprechende Buchstaben (-folgen) bestimmt
        // werden (Vgl. https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd. MMMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private String getFormattedDistance(float distance, String suffix) {
        // DecimalFormat erlaubt, ähnlich wie die SimpleDateFormat-Klasse, die anpassbare Darstellung
        // von numerischen Werten in Form lesbarer Textbausteine
        // (Vgl. https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html).
        DecimalFormat df = new DecimalFormat("##.00");
        return df.format(distance) + suffix;
    }

    private String getFormattedTime(float time, String suffix) {
        DecimalFormat df = new DecimalFormat("00");
        int minutes = (int) time;
        int seconds = (int) ((time - minutes) * 60);
        return df.format(minutes) + ":" + df.format(seconds) + suffix;
    }

    /**
     * Überladende Variante der getFormattedTime-Methode zur Nutzung ohne Suffix-String
     */
    private String getFormattedTime(float time) {
        return getFormattedTime(time, "");
    }


}
