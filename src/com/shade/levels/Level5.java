package com.shade.levels;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.DayPhaseTimer;
import com.shade.entities.mushroom.MushroomFactory;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LuminousEntity;

public class Level5 extends Model {

    private static final int SECONDS_PER_DAY = 120000;
    private int timer;
    public DayPhaseTimer dayTimer;

    public Level5(int w, int h, int c) throws SlickException {
        super(w, h, c);
        dayTimer = new DayPhaseTimer(SECONDS_PER_DAY);

        LevelSerial l = new LevelSerial();
        for (LuminousEntity e : l.deserialize("levels/level-5.xml")) {
            add(e);
        }
    }
    
    @Override
    public void update(StateBasedGame game, int delta) {
        super.update(game, delta);
        timer += delta;
    }

    @Override
    public GlobalLight getGlobalLight() {
        return new GlobalLight(12, (float) (4 * Math.PI / 3), SECONDS_PER_DAY, dayTimer);
    }

    @Override
    public MushroomFactory getMushroomFactory() {
        return new MushroomFactory(12, .000);
    }

    @Override
    public boolean levelClear() {
        return timer > SECONDS_PER_DAY;
    }

}
