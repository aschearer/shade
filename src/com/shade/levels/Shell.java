package com.shade.levels;

import org.newdawn.slick.SlickException;

import com.shade.entities.mushroom.MushroomFactory;
import com.shade.lighting.LuminousEntity;

/**
 * A container model which will be populated by deserializing a level.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class Shell extends Model {

    private static final int WIDTH = 8;
    private static final int HEIGHT = 6;
    private static final int CELL = 100;
    private MushroomFactory factory;
    private int par;

    public Shell(String path) throws SlickException {
        super(WIDTH, HEIGHT, CELL);
        LevelSerial l = new LevelSerial(path);
        for (LuminousEntity e : l.entities()) {
            add(e);
        }
        factory = l.factory();
        par = l.par();
    }

    @Override
    public MushroomFactory getMushroomFactory() {
        return factory;
    }
    
    @Override
    public int getPar() {
        return par;
    }
}
