package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.crash.util.CrashGeom;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Repelable;
import com.shade.levels.Model;
import com.shade.lighting.LuminousEntity;

abstract public class Obstacle extends Body implements LuminousEntity, Repelable{
    
    public static int maxRank;
    
    protected Model model;
    protected int zindex;
    protected Image sprite;
    
    private int rank = -1;
    
    public int rank() {
        if (rank < 0) {
            calculateRank();
        }
        return 1;
//        return rank;
    }
    
    private void calculateRank() {
        Basket b = (Basket) model.getEntitiesByRole(Roles.BASKET.ordinal())[0];
        
        float distance = CrashGeom.distance2(this, b);
        rank = (int) Math.ceil(distance / 100);
        if (rank > 1 && this instanceof Fence) {
            rank--;
        }
        maxRank = Math.max(rank, maxRank);
    }

    public int getRole() {
        return Roles.OBSTACLE.ordinal();
    }
    
    public void onCollision(Entity obstacle) {

    }
    
    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
    }
    
    public int getZIndex() {
        return zindex;
    }
    
    public float getLuminosity() {
        // not important for an obstacle
        return 0;
    }

    public void setLuminosity(float l) {
        // not important for an obstacle
    }

    public void addToLevel(Level<?> l) {
        model = (Model) l;
    }

    public void removeFromLevel(Level<?> l) {
        // not important for an obstacle
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
