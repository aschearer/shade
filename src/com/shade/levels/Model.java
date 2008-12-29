package com.shade.levels;

import com.shade.crash.CrashLevel;
import com.shade.entities.mushroom.MushroomFactory;

public abstract class Model extends CrashLevel {

    public Model(int w, int h, int c) {
        super(w, h, c);
    }
    
    abstract public MushroomFactory getMushroomFactory();
    
}
