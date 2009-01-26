package com.shade.controls;

import java.util.prefs.Preferences;

public class SerialStats {
    
    public static int read(String stat) {
        Preferences stats = Preferences.systemNodeForPackage(SerialStats.class);
        return stats.getInt(stat, 0);
    }
    
    public static void add(String stat, int value) {
        value += read(stat);
        write(stat, value);
    }

    public static void write(String stat, int value) {
        Preferences stats = Preferences.systemNodeForPackage(SerialStats.class);
        stats.putInt(stat, value);
    }
    
    public static void reset(String stat) {
        write(stat, 0);
    }
}
