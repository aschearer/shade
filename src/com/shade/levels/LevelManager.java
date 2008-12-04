package com.shade.levels;

import java.util.Iterator;

import org.newdawn.slick.SlickException;

import com.shade.base.Level;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Reflection;

/**
 * Convenient iterator for fetching the next level.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LevelManager implements Iterator<Level<LuminousEntity>> {

    /* The dimensions for the grid underlying each level. */
    private int width, height, cell;

    /* List of levels to create using reflection. */
    private String[] levels = {
        "com.shade.levels.Level1"
    };

    /* Pointer into the list of levels. */
    private int currentLevel;

    public LevelManager(int w, int h, int c) {
        width = w;
        height = h;
        cell = c;
        currentLevel = 0;
    }

    public boolean hasNext() {
        return currentLevel < levels.length;
    }

    @SuppressWarnings("unchecked")
    public Level<LuminousEntity> next() {
        String classname = levels[currentLevel];
        Object o = null;
        try {
            o = Reflection.getInstance(classname, width, height, cell);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        currentLevel++;
        return (Level<LuminousEntity>) o;
    }

    public void remove() {
        throw new RuntimeException("Method not supported.");
    }


}
