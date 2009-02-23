package com.shade.levels;

import org.newdawn.slick.SlickException;

/**
 * Convenient iterator for fetching the next level.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LevelManager {


    public static final int NUM_LEVELS = 11;
    
    /* List of levels to create using reflection. */
    private String[] levels = {
        "levels/level-0.xml",
        "levels/level-1.xml",
        "levels/level-2.xml",
//        "com.shade.levels.Level3",
//        "com.shade.levels.Level4",
//        "com.shade.levels.Level5",
//        "com.shade.levels.Level6",
//        "com.shade.levels.Level7",
        "levels/level-8.xml",
        //"levels/level-19.xml",
        "levels/level-10.xml",
        "levels/level-11.xml",
        "levels/level-12.xml",
        "levels/level-13.xml",
        "levels/level-14.xml",
        "levels/level-15.xml",
        "levels/level-16.xml"

        
    };

  
    public Model get(int i) {
        String path = levels[i];
        Shell level = null;
        try {
            level = new Shell(path);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        return level;
    }


    public int size() {
        return levels.length;
    }
}
