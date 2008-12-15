package com.shade.levels;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.entities.mushroom.MushroomFactory;
import com.shade.lighting.GlobalLight;

public class Level3 extends Model {

    private static final int SECONDS_PER_DAY = 60000;
    private int timer;

    public Level3(int w, int h, int c) throws SlickException {
        super(w, h, c);

        add(new Player(520, 150));
        add(new Basket(480, 100, 65, 40));

        add(new Fence(720, 50, 11, 120, 5));
        add(new Fence(720, 190, 11, 120, 5));

        add(new Dome(600, 425, 25, 6));
        add(new Dome(650, 350, 25, 6));
        add(new Dome(710, 460, 50, 10));
        add(new Dome(550, 250, 60, 13));
        add(new Block(385, 350, 100, 100, 14));
        
        add(new Dome(320, 225, 35, 7));
        add(new Dome(260, 360, 50, 10));

        add(new Fence(150, 500, 120, 11, 5));
        add(new Fence(300, 500, 120, 11, 5));
        add(new Fence(450, 500, 120, 11, 5));

        add(new Block(75, 75, 125, 125, 15));
        add(new Block(250, 100, 50, 50, 5));
        add(new Block(100, 250, 50, 50, 5));
        add(new Block(100, 350, 50, 50, 5));

    }
    
    @Override
    public void update(StateBasedGame game, int delta) {
        super.update(game, delta);
        timer += delta;
    }

    @Override
    public GlobalLight getGlobalLight() {
        return new GlobalLight(12, (float) (4 * Math.PI / 3), SECONDS_PER_DAY);
    }

    @Override
    public MushroomFactory getMushroomFactory() {
        return new MushroomFactory(8, .002);
    }

    @Override
    public boolean levelClear() {
        return timer > SECONDS_PER_DAY;
    }

}
