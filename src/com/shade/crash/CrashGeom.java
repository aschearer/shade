package com.shade.crash;

import com.crash.Body;
import com.shade.util.Geom;

public class CrashGeom {

    /** Returns the distance squared between the two body's centers. */
    public static float distance2(Body one, Body two) {
        float x = one.getXCenter() - two.getXCenter();
        x = x * x;
        float y = one.getYCenter() - two.getYCenter();
        y = y * y;
        return (x + y);
    }

    public static float distance2(Body one, float x2, float y2) {
        float x = one.getXCenter() - x2;
        x = x * x;
        float y = one.getYCenter() - y2;
        y = y * y;
        return (x + y);
    }

    public static float distance(Body one, Body two) {
        return (float) Math.sqrt(distance2(one, two));
    }

    public static float distance(Body one, float x2, float y2) {
        return (float) Math.sqrt(distance2(one, x2, y2));
    }

    public static float calculateAngle(Body one, Body two) {
        float x1 = one.getXCenter();
        float y1 = one.getYCenter();
        float x2 = two.getXCenter();
        float y2 = two.getYCenter();
        return Geom.calculateAngle(x1, y1, x2, y2);
    }

    public static float calculateAngle(Body one, float x2, float y2) {
        float x1 = one.getXCenter();
        float y1 = one.getYCenter();
        return Geom.calculateAngle(x1, y1, x2, y2);
    }

}
