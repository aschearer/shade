package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;

public class Block extends Body {
    
    private static final float H_WIDTH = 40;
    private static final float WIDTH = 80;
    private static final float H_HEIGHT = 40;
    private static final float HEIGHT = 80;

    public Block(float x, float y) {
        initShape(x, y);
    }
    
    private void initShape(float x, float y) {
        shape = new Rectangle(x - H_WIDTH, y - H_HEIGHT, WIDTH, HEIGHT);
    }
    
    public Role getRole() {
        return Role.OBSTACLE;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub
        
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(Graphics g) {
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

}
