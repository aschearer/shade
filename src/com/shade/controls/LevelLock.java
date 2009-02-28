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
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
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
    
    public boolean allUnlocked() {
        for (int i = 0; i < unlocked.length; i++) {
            if (!unlocked[i]) {
                return false;
            }
        }
        return true;
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
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
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
        unlocked[0] = true;
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
    
    public void testAndUnlockLevels() {
            int clear123 = 0;
            clear123 += SerialStats.read("level-1-clear");
            clear123 += SerialStats.read("level-2-clear");
            clear123 += SerialStats.read("level-3-clear");
            
            if (clear123 >= 1) {
                unlock(5); // beat one of the first 3 levels
            }
            
            if (SerialStats.read("golden-mushrooms-collected") >= 25) {
                unlock(6); // collect 25 gold mushrooms 
            }
            
            if (SerialStats.read("mushrooms-collected") >= 100) {
                unlock(7); // collect 100 mushrooms
            }
            
            int clear456 = 0;
            clear456 += SerialStats.read("level-4-clear");
            clear456 += SerialStats.read("level-5-clear");
            clear456 += SerialStats.read("level-6-clear");
            
            if (clear456 == 3) {
                unlock(8); // beat each of the second three
            }
                
            if (SerialStats.read("level-mushrooms-collected") >= 40) {
                unlock(9); // collect 40 mushrooms in a single level
            }

            int clear789 = 0;
            clear789 += SerialStats.read("level-7-clear");
            clear789 += SerialStats.read("level-8-clear");
            clear789 += SerialStats.read("level-9-clear");
            if (clear123 == 3 && clear456 == 3 && clear789 == 3) {
                unlock(10); // beat levels 1-9
            }
        }
    
    public boolean newLevelUnlocked() {
        int clear123 = 0;
        clear123 += SerialStats.read("level-1-clear");
        clear123 += SerialStats.read("level-2-clear");
        clear123 += SerialStats.read("level-3-clear");
        
        if (!isUnlocked(5) && clear123 >= 1) {
            return true; // beat one of the first 3 levels
        }
        
        if (!isUnlocked(6) && SerialStats.read("golden-mushrooms-collected") >= 25) {
            return true; // collect 25 gold mushrooms 
        }
        
        if (!isUnlocked(7) && SerialStats.read("mushrooms-collected") >= 100) {
            return true; // collect 100 mushrooms
        }
        
        int clear456 = 0;
        clear456 += SerialStats.read("level-4-clear");
        clear456 += SerialStats.read("level-5-clear");
        clear456 += SerialStats.read("level-6-clear");
        
        if (!isUnlocked(8) && clear456 == 3) {
            return true; // clear the second three levels
        }
            
        if (!isUnlocked(9) && SerialStats.read("level-mushrooms-collected") >= 40) {
            return true; // collect 40 mushrooms in a single level
        }

        int clear789 = 0;
        clear789 += SerialStats.read("level-7-clear");
        clear789 += SerialStats.read("level-8-clear");
        clear789 += SerialStats.read("level-9-clear");
        if (!isUnlocked(10) && clear123 == 3 && clear456 == 3 && clear789 == 3) {
            return true; // beat levels 1-9
        }
        
        return false;
    }
    
    public static void main(String[] args) {
        LevelLock lock = new LevelLock();
        lock.freeFirst(9); // counting 0
        
        LevelLock l = new LevelLock();
        
        for (int i = 0; i < l.unlocked.length; i++) {
            if (l.unlocked[i]) {
                System.out.println("level " + i + " unlocked");
            }
        }
    }
}
