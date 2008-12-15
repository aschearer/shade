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

public class Level1 extends Model {

    private static final int SECONDS_PER_DAY = 60000;
    private int timer;

    public Level1(int w, int h, int c) throws SlickException {
        super(w, h, c);

        add(new Player(350, 250));
        add(new Basket(400, 250, 65, 40));

        add(new Block(55, 355, 125, 125, 16));
        add(new Block(224, 424, 56, 56, 6));
        add(new Block(324, 424, 56, 56, 6));
        add(new Block(75, 225, 56, 56, 6));
        add(new Block(545, 330, 80, 80, 10));
        add(new Block(445, 460, 80, 80, 10));
        // domes
        add(new Dome(288, 165, 32, 7));
        add(new Dome(180, 95, 44, 10));
        add(new Dome(300, 65, 25, 6));
        add(new Dome(710, 80, 28, 6));
        add(new Dome(600, 100, 40, 9));
        add(new Dome(680, 220, 60, 13));
        // fences
        add(new Fence(250, 250, 11, 120, 5));
        add(new Fence(390, 140, 120, 11, 5));
        add(new Fence(715, 368, 11, 120, 5));
        add(new Fence(50, 50, 11, 120, 5));
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
