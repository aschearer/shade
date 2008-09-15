package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

public class Dome extends ShadowCaster {

    public Dome(float x, float y, float r, float d) {
        initShape(x, y, r);
        depth = d;
    }

    private void initShape(float x, float y, float r) {
        shape = new Circle(x, y, r);
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public Role getRole() {
        return Role.OBSTACLE;
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub
        
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(Graphics g) {
        renderShadow(g);
        g.fill(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

}
