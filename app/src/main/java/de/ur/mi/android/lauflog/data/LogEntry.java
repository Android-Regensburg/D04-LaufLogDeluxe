package de.ur.mi.android.lauflog.data;

import java.util.Comparator;
import java.util.Date;

public class LogEntry {

    private final Date date;
    private final float timeInMinutes;
    private final float distanceInKilometers;

    public LogEntry(Date date, float timeInMinutes, float distanceInKilometers) {
        this.date = date;
        this.timeInMinutes = timeInMinutes;
        this.distanceInKilometers = distanceInKilometers;
    }

    public Date getDate() {
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
                long thisDateInMs = o1.getDate().getTime();
                long thatDateInMs = o2.getDate().getTime();
                return (int) (thisDateInMs - thatDateInMs);
            }
        };
        return comparator;
    }

    public static Comparator<LogEntry> getDistanceComparator() {
        Comparator  comparator = new Comparator<LogEntry>() {
            @Override
            public int compare(LogEntry o1, LogEntry o2) {
                return (int) (o2.getDistance() - o1.getDistance());
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
