package com.shade.util;

import org.newdawn.slick.geom.Vector2f;

/** Static methods for performing geometry operations. */
public class Geom {

    /** The angle should be in radians. */
    public static Vector2f calculateVector(float magnitude, float angle) {
        Vector2f v = new Vector2f();
        v.x = (float) Math.sin(angle);
        v.x *= magnitude;
        v.y = (float) -Math.cos(angle);
        v.y *= magnitude;
        return v;
    }
}
