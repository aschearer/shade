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
        "levels/Level-1.xml",
        "levels/Level-2.xml",
//        "com.shade.levels.Level3",
//        "com.shade.levels.Level4",
//        "com.shade.levels.Level5",
//        "com.shade.levels.Level6",
//        "com.shade.levels.Level7",
        "levels/Level-8.xml",
//        "com.shade.levels.Level9",
        "levels/Level-10.xml",
        "levels/Level-11.xml",
        "levels/Level-12.xml",
        "levels/Level-13.xml",
        "levels/Level-14.xml"
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
