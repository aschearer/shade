package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.shadows.ShadowCaster;

public class Shrub extends Linkable implements ShadowCaster {
    
    private static final int MIN_Z = 3;
    private static final int MAX_Z = 9;
    
    private static final float MIN_SIZE = 9f;
    private static final float MAX_SIZE = 20f;
    
    private int zIndex;
    
    public Shrub(float x, float y) {
        initShape(x, y);
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, MIN_SIZE);
    }

    public Shape castShadow(float direction, float depth) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getZIndex() {
        return zIndex;
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

    public void repel(Entity repellee) {
        // TODO Auto-generated method stub
        
    }

    public void render(StateBasedGame game, Graphics g) {
        // TODO Auto-generated method stub
        
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub
        
    }

    public int compareTo(ShadowCaster o) {
        // TODO Auto-generated method stub
        return 0;
    }
    
	public Vector2f getPosition() {
		// TODO Auto-generated method stub
		return new Vector2f(getCenterX(),getCenterY());
	}

}
