package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Level;
import com.shade.base.SimpleLevel;
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
        level = new SimpleLevel();
        
        Player player = new Player(400, 300);
        level.add(player);
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
