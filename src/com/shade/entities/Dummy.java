package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.lighting.LuminousEntity;

/**
 * A class which doesn't do anything but render.
 *
 * @author Alexander Schearer
 */
public class Dummy extends Body implements LuminousEntity {
    
    private float x, y;
    private Image sprite;

    public Dummy(float x, float y, Image s) {
        this.x = x;
        this.y = y;
        sprite = s;
        float width = s.getWidth();
        float height = s.getHeight();
        shape = new Rectangle(x - width / 2, y - height / 2, width, height);
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public float getLuminosity() {
        return 0;
    }

    public int getZIndex() {
        return 0;
    }

    public void setLuminosity(float l) {
        
    }

    public void addToLevel(Level<?> l) {
        
    }

    public int getRole() {
        return Roles.DUMMY.ordinal();
    }

    public void onCollision(Entity obstacle) {
        
    }

    public void removeFromLevel(Level<?> l) {
        
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.drawCentered(x, y);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

    public int compareTo(LuminousEntity o) {
        return getZIndex() - o.getZIndex();
    }

}
