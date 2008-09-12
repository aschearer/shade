package com.shade.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;

public class Basket extends Body {
    
    private static final float RADIUS = 24f;

    public Basket(float x, float y) {
        initShape(x, y);
    }

    private void initShape(float x, float y) {
        shape = new Circle(x - RADIUS, y - RADIUS, RADIUS);
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public Role getRole() {
        return Role.BASKET;
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub
        
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.darkGray);
        g.fill(shape);
        g.setColor(c);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

}
