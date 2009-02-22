package com.shade.controls;

import java.util.prefs.Preferences;

import com.shade.levels.LevelManager;

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
    
    public static void main(String[] args) {
        for (int i = 0; i < LevelManager.NUM_LEVELS; i++) {
            String stat = "level-" + i + "-clear";
            SerialStats.reset(stat);
        }
        
        SerialStats.reset("golden-mushrooms-collected");
        SerialStats.reset("mushrooms-collected");
        SerialStats.reset("level-mushrooms-collected");
    }
}
