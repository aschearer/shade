package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

public class Mushroom implements Entity {
    
    private enum Status { IDLE };
    
    private static final float H_RADIUS = 3f;
    private static final float SCALE_INCREMENT = .1f;
    private static final float MAX_SCALE = 5f;
    
    private Circle shape;

    private float scale;
    private Status currentStatus;
    private float timer;
    
    public Mushroom(float x, float y) {
        initShape(x, y);
        currentStatus = Status.IDLE;
        scale = 1;
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
        timer += delta;
        if (timer > 300 && currentStatus == Status.IDLE) {
            timer = 0;
            if (scale < MAX_SCALE) {
                scale += SCALE_INCREMENT;
                grow();
            }
            /* Turn to a monster */
        }
    }

    private void grow() {
        shape.setRadius(H_RADIUS * scale);
    }

}
