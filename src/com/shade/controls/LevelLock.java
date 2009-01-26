package com.shade.controls;

import java.util.prefs.Preferences;

import com.shade.levels.LevelManager;

/**
 * Controls which levels are available to the player.
 * 
 * This object persists through the user preferences.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LevelLock {

    private static final String LEVELS_KEY = "unlocked-levels";
    private static final String LEVEL_LOCKED = "0";
    private static final String LEVEL_UNLOCKED = "1";
    private static final String DELIMITER = ",";
    private static final String STRING_EMPTY = "";
    
    private boolean[] unlocked;

    public LevelLock() {
        unlocked = new boolean[LevelManager.NUM_LEVELS];
        unlocked[0] = true;
        unlocked = deserialize();
    }

    private boolean[] deserialize() {
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        String serial = prefs.get(LEVELS_KEY, STRING_EMPTY);
        String[] levels = serial.split(DELIMITER);
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(LEVEL_UNLOCKED)) {
                unlocked[i] = true;
            }
        }
        return unlocked;
    }
    
    public boolean isUnlocked(int level) {
        return unlocked[level];
    }
    
    public void unlock(int level) {
        if (unlocked[level]) {
            return;
        }
        unlocked[level] = true;
        save();
    }
    
    public void save() {
        String[] levels = new String[unlocked.length];
        for (int i = 0; i < unlocked.length; i++) {
            if (unlocked[i]) {
                levels[i] = LEVEL_UNLOCKED;
            } else {
                levels[i] = LEVEL_LOCKED;
            }
        }
        String serial = implode(levels, DELIMITER);
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        prefs.put(LEVELS_KEY, serial);
    }

    private String implode(String[] strings, String glue) {
        StringBuilder result = new StringBuilder(strings[1]);
        for (int i = 1; i < strings.length; i++) {
            result.append(DELIMITER);
            result.append(strings[i]);
        }
        return result.toString();
    }
    
    public void resetLocks() {
        unlocked = new boolean[LevelManager.NUM_LEVELS];
        unlocked[0] = false;
        save();
    }
    
    public void freeFirst(int n) {
        resetLocks();
        for (int i = 1; i < LevelManager.NUM_LEVELS; i++) {
            if (i < n) {
                unlocked[i] = true;
            }
        }
        save();
    }
    
    public static void main(String[] args) {
        LevelLock lock = new LevelLock();
        lock.freeFirst(7); // counting 0
    }
}
