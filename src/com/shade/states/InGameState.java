package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Level;
import com.shade.crash.CrashLevel;
import com.shade.crash.Grid;
import com.shade.entities.Mushroom;
import com.shade.entities.Player;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Level level;
    
    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        level = new CrashLevel(new Grid(8, 6, 100));
        
        Player player = new Player(400, 300);
        level.add(player);
        
        Mushroom[] m = new Mushroom[3];
        m[0] = new Mushroom(100, 100);
        m[1] = new Mushroom(700, 300);
        m[2] = new Mushroom(300, 450);
        
        for (int i = 0; i < m.length; i++) {
            level.add(m[i]);
        }
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        level.render(g);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        level.update(game, delta);
    }

}
