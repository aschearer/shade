package com.shade.entities.mole;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.CrashLevel;
import com.shade.entities.Linkable;
import com.shade.entities.Roles;
import com.shade.lighting.LuminousEntity;

public class Hole extends Linkable {
    
    private int timer;
    private float luminosity;
    private CrashLevel level;

    public Hole(float x, float y) {
        initShape(x, y);
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 15);
    }

    public float getLuminosity() {
        return luminosity;
    }
    
    public void setLuminosity(float l) {
        luminosity = l;
    }

    public int getZIndex() {
        return 0;
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }
    
    public void addToLevel(Level<?> l) {
        level = (CrashLevel) l;
    }
    
    public void removeFromLevel(Level<?> l) {
        // do nothing
    }

    public int getRole() {
        return Roles.BASKET.ordinal();
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PICKED_MUSHROOM.ordinal()) {
            // don't even need to do anything...
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        g.setColor(Color.black);
        g.fill(shape);
        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (timer > 8000) {
            level.remove(this);
        }
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
