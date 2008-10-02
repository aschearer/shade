package com.shade.crash;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import com.shade.base.Entity;

/**
 * A physical body which can collide with other bodies. Bodies are checked
 * against each other for intersection and notified when intersection occurs.
 */
public abstract class Body implements Entity {

    /** The underlying shape of this body, used to perform intersection tests. */
    protected Shape shape;
    protected float xVelocity, yVelocity;

    public float getCenterX() {
        return shape.getCenterX();
    }

    public float getCenterY() {
        return shape.getCenterY();
    }
    
    public float getX() {
        return shape.getX();
    }
    
    public float getY() {
        return shape.getY();
    }

    public float getWidth() {
        return shape.getMaxX() - shape.getX();
    }

    public float getHeight() {
        return shape.getMaxY() - shape.getY();
    }
    
    public void setVelocity(float x, float y){
    	xVelocity = x;
    	yVelocity = y;
    }
    
    public Vector2f getVelocity(){
    	return new Vector2f((float)xVelocity, (float)yVelocity);
    }
    
    @Override
    public String toString() {
        return "Body[" + getCenterX() + "," + getCenterY() + "]";
    }
    
    public void move(double x, double y){
    	float xf = (float)x;
    	float yf = (float)y;
    	//xVelocity += xf;
    	//yVelocity +=yf;
    	float endx = getCenterX()-getWidth()/2+xf;
    	float endy = getCenterY()-getHeight()/2+yf;
    	shape.setLocation(endx,endy);
        
    }
}
