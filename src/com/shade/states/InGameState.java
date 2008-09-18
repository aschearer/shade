package com.shade.states;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.crash.Grid;
import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.shadows.ShadowCaster;
import com.shade.shadows.ShadowLevel;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image backgroundSprite, trimSprite;
    private ShadowLevel level;

    private int sunTimer, totalTime;
    private float sunAngle;

    private ShadowCaster[] b;

    private Basket basket;
    
    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        sunAngle = 2.5f;
        Grid grid = new Grid(8, 6, 100);
        level = new ShadowLevel(grid);
        
        /* TODO there's a rendering priority problem involved here... */
        basket = new Basket(456, 232, 65, 40);
        grid.add(basket);
        
        b = new ShadowCaster[14];
        // boxes
        b[0] = new Block(150, 300, 135, 135, 16);
        b[1] = new Block(324, 376, 56, 56, 6);
        b[2] = new Block(416, 376, 56, 56, 6);
        b[3] = new Block(508, 325, 56, 56, 6);
        b[4] = new Block(545, 450, 80, 80, 10);
        b[5] = new Block(445, 520, 80, 80, 10);
        // domes
        b[6] = new Block(175, -10, 88, 88, 8);
        b[7] = new Block(300, 18, 50, 50, 6);
        b[8] = new Block(278, 90, 64, 64, 7);
        b[9] = new Block(618, 15, 80, 80, 8);
        b[10] = new Block(700, 102, 120, 120, 11);
        // fences
        b[11] = new Fence(150, 150, 11, 120, 6);
        b[12] = new Fence(390, 140, 120, 11, 6);
        b[13] = new Fence(700, 368, 11, 120, 6);
        
        for (int i = 0; i < b.length; i++) {
            level.add(b[i]);
        }
        
        grid.update();
        level.updateShadowscape(sunAngle);
        
        // add five mushrooms to start
        for (int i = 0; i < 5; i++) {
            level.plant();
        }
        
        Player player = new Player(400, 350, 14);
        level.add(player);
    }

    private void initSprites() throws SlickException {
        backgroundSprite = new Image("states/ingame/background.png");
        trimSprite = new Image("states/ingame/trim.png");
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        backgroundSprite.draw();
        basket.render(g);
        level.render(g);
        trimSprite.draw();
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        level.update(game, delta);
        basket.update(game, delta);
        totalTime += delta;
        sunTimer += delta;
        if (totalTime > 8000) {
            level.plant();            
            level.plant();            
            level.plant();
            totalTime = 0;
        }
        
//        if (sunTimer > 500) {
            sunTimer = 0;
            sunAngle += .0005f;
            level.updateShadowscape(sunAngle);
//        }
    }

}
