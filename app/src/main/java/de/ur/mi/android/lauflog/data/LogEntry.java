package de.ur.mi.android.lauflog.data;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;

public class LogEntry {

    private final LocalDateTime date;
    private final float timeInMinutes;
    private final float distanceInKilometers;

    public LogEntry(LocalDateTime date, float timeInMinutes, float distanceInKilometers) {
        this.date = date;
        this.timeInMinutes = timeInMinutes;
        this.distanceInKilometers = distanceInKilometers;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public float getTime() {
        return timeInMinutes;
    }

    public float getDistance() {
        return distanceInKilometers;
    }

    public float getPace() {
        return timeInMinutes / distanceInKilometers;
    }

    public static Comparator<LogEntry> getDateComparator() {
        Comparator  comparator = new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry o1, LogEntry o2) {
                long thisDateInMs = o1.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long thatDateInMs = o2.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                return (int) (thisDateInMs - thatDateInMs);
            }
        };
        return comparator;
    }

    public static Comparator<LogEntry> getDistanceComparator() {
        Comparator  comparator = new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry o1, LogEntry o2) {
                return (int) (o1.getDistance() - o2.getDistance());
            }
        };
        return comparator;
    }

    public static Comparator<LogEntry> getPaceComparator() {
        Comparator  comparator = new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry o1, LogEntry o2) {
                return (int) (o1.getPace() - o2.getPace());
            }
        };
        return comparator;
    }

}
