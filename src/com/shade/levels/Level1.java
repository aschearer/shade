package com.shade.levels;

import org.newdawn.slick.SlickException;

import com.shade.crash.CrashLevel;
import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.lighting.LuminousEntity;

public class Level1 extends CrashLevel<LuminousEntity> {

    public Level1(int w, int h, int c) throws SlickException {
        super(w, h, c);

        add(new Player(300, 200));
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

}
