package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Level;
import com.shade.crash.CrashLevel;
import com.shade.crash.Grid;
import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.entities.ShadowCaster;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image backgroundSprite, trimSprite;
    private Level level;
    
    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        level = new CrashLevel(new Grid(8, 6, 100));
        
        /* TODO there's a rendering priority problem involved here... */
        Basket bt = new Basket(456, 232, 65, 40);
        level.add(bt);
        
        Player player = new Player(400, 350, 16);
        level.add(player);
        
        ShadowCaster[] b = new ShadowCaster[14];
        // boxes
        b[0] = new Block(150, 300, 135, 135, 16);
        b[1] = new Block(324, 376, 56, 56, 6);
        b[2] = new Block(416, 376, 56, 56, 6);
        b[3] = new Block(508, 325, 56, 56, 6);
        b[4] = new Block(545, 450, 80, 80, 10);
        b[5] = new Block(445, 520, 80, 80, 10);
        // domes
        b[6] = new Dome(175, 36, 44, 8);
        b[7] = new Dome(300, 18, 25, 6);
        b[8] = new Dome(278, 90, 32, 7);
        b[9] = new Dome(618, 75, 40, 8);
        b[10] = new Dome(700, 162, 60, 11);
        // fences
        b[11] = new Fence(150, 150, 11, 120, 6);
        b[12] = new Fence(390, 140, 120, 11, 6);
        b[13] = new Fence(700, 368, 11, 120, 6);
        
        for (int i = 0; i < b.length; i++) {
            b[i].castShadow(-2.5f);
            level.add(b[i]);
        }
    }

    private void initSprites() throws SlickException {
        backgroundSprite = new Image("states/ingame/background.png");
        trimSprite = new Image("states/ingame/trim.png");
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        backgroundSprite.draw();
        level.render(g);
        trimSprite.draw();
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        level.update(game, delta);
    }

}
