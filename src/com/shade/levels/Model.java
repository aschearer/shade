package com.shade.levels;

import com.shade.crash.CrashLevel;
import com.shade.entities.mushroom.MushroomFactory;
import com.shade.lighting.GlobalLight;

public abstract class Model extends CrashLevel {

    public Model(int w, int h, int c) {
        super(w, h, c);
    }
    
    public abstract GlobalLight getGlobalLight();

    public abstract MushroomFactory getMushroomFactory();
}
