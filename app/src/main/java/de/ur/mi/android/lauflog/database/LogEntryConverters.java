package de.ur.mi.android.lauflog.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Sammlung von Konvertern, die notwendig sind, um die komplexen Objekt-Eigenschaften der LogEntry-
 * Instanzen in die Datenbank zu speichern. Die Room-Datenbank greift beim Speichern und Auslesen
 * entsprechender Werte automatisch auf diese Methoden zurück, wenn der entsprechend Verweis über
 * eine Annotation hergestellt wird (Vgl. LogEntryDatabase).
 */

public class LogEntryConverters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
