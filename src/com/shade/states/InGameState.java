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
import com.shade.entities.Mushroom;
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
        Basket bt = new Basket(450, 230, 65, 40);
        level.add(bt);
        
        Player player = new Player(400, 350, 16);
        level.add(player);
        
//        Mushroom[] m = new Mushroom[3];
//        m[0] = new Mushroom(100, 100);
//        m[1] = new Mushroom(700, 300);
//        m[2] = new Mushroom(300, 450);
//        
//        for (int i = 0; i < m.length; i++) {
//            level.add(m[i]);
//        }
        
        ShadowCaster[] b = new ShadowCaster[4];
        b[2] = new Dome(110, 120, 50, 8);
        b[0] = new Dome(230, 110, 25, 6);
        b[1] = new Dome(220, 200, 40, 7);
        b[3] = new Block(140, 320, 80, 80, 8);
        
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
