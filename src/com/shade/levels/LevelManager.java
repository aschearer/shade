package com.shade.levels;

import java.util.Iterator;

import org.newdawn.slick.SlickException;

/**
 * Convenient iterator for fetching the next level.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LevelManager implements Iterator<Model> {


    /* List of levels to create using reflection. */
    private String[] levels = {
        "levels/level-1.xml",
        "levels/level-2.xml",
//        "com.shade.levels.Level3",
//        "com.shade.levels.Level4",
//        "com.shade.levels.Level5",
//        "com.shade.levels.Level6",
//        "com.shade.levels.Level7",
        "levels/level-8.xml",
//        "com.shade.levels.Level9",
        "levels/level-10.xml",
        "levels/level-11.xml",
        "levels/level-12.xml",
        "levels/level-13.xml",
        "levels/level-14.xml",
        "levels/level-15.xml",
        "levels/level-16.xml"
    };

    /* Pointer into the list of levels. */
    private int currentLevel;

    public LevelManager() {
        currentLevel = 0;
    }

    public boolean hasNext() {
        return currentLevel < levels.length;
    }

    @SuppressWarnings("unchecked")
    public Model next() {
        String path = levels[currentLevel];
        Shell level = null;
        try {
            level = new Shell(path);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        currentLevel++;
        return level;
    }

    public void remove() {
        throw new RuntimeException("Method not supported.");
    }

    public void rewind() {
        currentLevel = 0;
    }


}
