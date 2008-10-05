package com.shade.entities.util;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;

public interface State {

    public boolean equals(Object o);

    public void enter();

    public void update(StateBasedGame game, int delta);

    public void render(Graphics g);

    public void onCollision(Entity obstacle);
    
}