package com.shade.crash;

import org.newdawn.slick.geom.Shape;

import com.shade.base.Entity;

/**
 * A physical body which can collide with other bodies. Bodies are checked
 * against each other for intersection and notified when intersection occurs.
 */
public abstract class Body implements Entity {

    /** The underlying shape of this body, used to perform intersection tests. */
    protected Shape shape;

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
}
