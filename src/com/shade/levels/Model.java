package com.shade.levels;

import com.shade.controls.DayPhaseTimer;
import com.shade.crash.CrashLevel;
import com.shade.entities.mushroom.MushroomFactory;
import com.shade.lighting.GlobalLight;

public abstract class Model extends CrashLevel {
	
	DayPhaseTimer daytimer;

    public Model(int w, int h, int c) {
        super(w, h, c);
    }
    
    public abstract GlobalLight getGlobalLight();

    public abstract MushroomFactory getMushroomFactory();
    
    public void setTimer(DayPhaseTimer t){
    	daytimer = t;
    }
    
    public DayPhaseTimer getTimer(){
    	return daytimer;
    }
}
