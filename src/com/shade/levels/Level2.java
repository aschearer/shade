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

public class Level2 extends Model {
    
    private static final int SECONDS_PER_DAY = 60000;
    private int timer;

    public Level2(int w, int h, int c) throws SlickException {
        super(w, h, c);
        
        add(new Player(150, 150, true));
        add(new Basket(100, 100, 65, 40));
        
        add(new Fence(250, 100, 120, 11, 5));
        add(new Block(430, 75, 56, 56, 6));
        add(new Fence(550, 100, 120, 11, 5));
        
        add(new Fence(150, 500, 120, 11, 5));
        add(new Block(330, 475, 56, 56, 6));
        add(new Fence(450, 500, 120, 11, 5));
        
        add(new Fence(75, 190, 11, 120, 5));
        add(new Block(75, 325, 125, 125, 16));
        
        add(new Block(200, 190, 56, 56, 6));
        add(new Block(300, 190, 56, 56, 6));
        add(new Block(300, 315, 56, 56, 6));
        add(new Block(400, 315, 56, 56, 6));
        
        add(new Dome(600, 425, 25, 6));
        add(new Dome(650, 350, 25, 6));
        add(new Dome(710, 460, 44, 10));
        add(new Dome(550, 250, 60, 13));
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
        return new MushroomFactory(16, .002);
    }

    @Override
    public boolean levelClear() {
        return timer > SECONDS_PER_DAY;
    }

}
