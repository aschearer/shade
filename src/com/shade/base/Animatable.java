package com.shade.base;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Any object which can be written to the screen and animated. 
 * 
 * @author Alex Schearer <aschearer@gmail.com>
 */
public interface Animatable {
    
    public void render(Graphics g);

    public void update(StateBasedGame game, int delta);
}
