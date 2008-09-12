package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

public class Mushroom implements Entity {
    
    private static final float H_RADIUS = 3f;
    
    private Shape shape;
    
    public Mushroom(float x, float y) {
        initShape(x, y);
    }
    
    private void initShape(float x, float y) {
        shape = new Circle(x - H_RADIUS, y - H_RADIUS, H_RADIUS);
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
        
    }
    
    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub
        
    }

    public void render(Graphics g) {
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

}
