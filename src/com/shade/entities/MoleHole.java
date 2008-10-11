package com.shade.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.shadows.ShadowEntity;

public class MoleHole extends Linkable implements ShadowEntity {
    
    private Image sprite;
    private int timer;
    private ShadowIntensity shadowStatus;
    private Level level;
    
    public MoleHole(float x, float y, float r) {
        initShape(x, y, r);
    }

    private void initShape(float x, float y, float r) {
        shape = new Circle(x, y, r);
    }
    
    private void initResources() {
        
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public Role getRole() {
        return Role.BASKET;
    }
    
    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.MUSHROOM) {
            Mushroom m = (Mushroom) obstacle;
            m.detach();
            level.remove(m);
        }
    }
    

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(StateBasedGame game, Graphics g) {
//        sprite.draw(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.black);
        g.fill(shape);
        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (timer > 12000) {
            level.remove(this);
        }
    }

    public void repel(Entity repellee) {
        // TODO Auto-generated method stub
        
    }
    
    public int getZIndex() {
        return 1;
    }

    public int compareTo(ShadowEntity s) {
        return getZIndex() - s.getZIndex();
    }
}
