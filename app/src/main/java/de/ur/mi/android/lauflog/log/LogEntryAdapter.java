package de.ur.mi.android.lauflog.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.ur.mi.android.lauflog.R;

/**
 * Adapter zur Anzeige einer Liste von LogEntries als Inhalt einem RecyclerView im User Interface
 */
public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder> {

    // Liste der LogEntries, die über den Adapter verwaltet werden.
    private ArrayList<LogEntry> entries;
    private Context context;

    /**
     * Konkrete Implementierung der abstrakten ViewHolder-Klasse. Instanzen von LogEntryViewHolder
     * werden vom Adapter an den RecyclerView weitergegeben um die im Adapter gespeicherten Daten
     * im User Interface anzuzeigen. Jedes Datum (hier jeder Eintrag in der LogEntry-Liste) wird
     * dabei durch einen eigenen ViewHolder dargestellt.
     */
    public static class LogEntryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public View entryView;

        public LogEntryViewHolder(View v) {
            super(v);
            entryView = v;
        }
    }

    public LogEntryAdapter(ArrayList<LogEntry> entries, Context context) {
        this.entries = entries;
        this.context = context;
    }

    /**
     * Ersettz die aktuell im Adapter gespeicherten Daten (LogEntry-Liste) und informiert das
     * angeschlossene RecyclerView.
     */
    public void setEntries(ArrayList<LogEntry> entries) {
        this.entries = entries;
        this.notifyDataSetChanged();
    }

    /**
     * Erstellt bei Bedarf einen neuen ViewHolder für das angeschlossene RecyclerView.
     */
    @Override
    public LogEntryAdapter.LogEntryViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log_entry, parent, false);
        LogEntryViewHolder vh = new LogEntryViewHolder(v);
        return vh;
    }

    /**
     * Trägt bei Bedard die Informationen (Streckenlänge, Geschwindikgeit, Zeit) eines einzelnen
     * Eintrags in den vorbereiteten ViewHolder ein.
     */
    @Override
    public void onBindViewHolder(LogEntryViewHolder holder, int position) {
        LogEntry entry = entries.get(position);
        TextView dateView = holder.entryView.findViewById(R.id.log_entry_date);
        TextView distanceView = holder.entryView.findViewById(R.id.log_entry_distance);
        TextView timeView = holder.entryView.findViewById(R.id.log_entry_time);
        TextView paceView = holder.entryView.findViewById(R.id.log_entry_pace);
        dateView.setText(getFormattedDate(entry.date));
        distanceView.setText(getFormattedDistance(entry.distanceInKilometers, context.getString(R.string.kilometer_suffix)));
        timeView.setText(getFormattedTime(entry.timeInMinutes, context.getString(R.string.minutes_suffix)));
        paceView.setText(getFormattedTime(entry.getPace()));
    }

    /**
     * Gibt die insgesamte Anzahl an Einträge zurück, die im RecyclerView dargestellt werden sollen.
     *
     * @return
     */
    public int getItemCount() {
        return entries.size();
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
     * Überladende Variante der getFormattedTime-Methode zur Nutzung mit leerem Suffix-String
     */
    private String getFormattedTime(float time) {
        return getFormattedTime(time, "");
    }


}
