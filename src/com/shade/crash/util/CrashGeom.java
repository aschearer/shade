package com.shade.crash.util;

import com.shade.util.Geom;
import com.shade.crash.Body;

public class CrashGeom {

    /** Returns the distance squared between the two body's centers. */
    public static float distance2(Body one, Body two) {
        float x = one.getCenterX() - two.getCenterX();
        x = x * x;
        float y = one.getCenterY() - two.getCenterY();
        y = y * y;
        return (x + y);
    }

    public static float calculateAngle(Body one, Body two) {
        float x1 = one.getCenterX();
        float y1 = one.getCenterY();
        float x2 = two.getCenterX();
        float y2 = two.getCenterY();
        return Geom.calculateAngle(x1, y1, x2, y2);
    }
}
