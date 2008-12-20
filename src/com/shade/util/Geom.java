package com.shade.util;

import org.newdawn.slick.geom.Vector2f;

/** Static methods for performing geometry operations. */
public class Geom {

    /** The angle should be in radians. */
    public static Vector2f calculateVector(float magnitude, float angle) {
        Vector2f v = new Vector2f();
        v.x = (float) Math.sin(angle);
        v.x *= magnitude;
        v.y = (float) - Math.cos(angle);
        v.y *= magnitude;
        return v;
    }

    /**
     * From x,y -> x1,y1
     *
     * @return The value is in radians.
     */
    public static float calculateAngle(float x, float y, float x1, float y1) {
        double angle = Math.atan2(y - y1, x - x1);
        return (float) angle + 1.5f;
    }

    /**
     * Return the distance squared between two points.
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @return
     */
    public static float distance2(float x, float y, float x1, float y1) {
        x = (float) Math.pow(x - x1, 2);
        y = (float) Math.pow(y - y1, 2);
        return (x + y);
    }
    
    public static float distance2(float[] p1, float x, float y) {
        return distance2(p1[0], p1[1], x, y);
    }
}
