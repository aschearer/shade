package com.shade.entities.mushroom;

import java.util.Arrays;

import com.crash.Body;
import com.shade.crash.CrashGeom;
import com.shade.entities.Linkable;

public class WrappingUtils {

    /**
     * Return true if this body and the target are further apart than the
     * threshold.
     *
     * @param target
     * @param threshold
     * @return
     */
    public static boolean overThreshold(Body shroom, Body target,
                                           float threshold) {
        float[] d = new float[3];

        d[0] = CrashGeom.distance2(target, shroom);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (shroom.getX() < target.getX()) {
            d[1] = CrashGeom.distance2(target, shroom.getXCenter() + 800,
                                       shroom.getYCenter());
        } else {
            d[1] = CrashGeom.distance2(shroom, target.getXCenter() + 800,
                                       target.getYCenter());
        }

        // if I'm above my target
        if (shroom.getY() < target.getY()) {
            d[2] = CrashGeom.distance2(target, shroom.getXCenter(), shroom
                                       .getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(shroom, target.getXCenter(), target
                                       .getYCenter() + 600);
        }

        Arrays.sort(d);

        return (d[0] > threshold);
    }

    /**
     * Return the angle the shroom must move to reach the target accounting for
     * wrapping.
     *
     * @param shroom
     * @param target
     * @return
     */
    public static float calculateAngle(Body  shroom, Linkable target) {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(target, shroom);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (shroom.getX() < target.getX()) {
            d[1] = CrashGeom.distance2(target, shroom.getXCenter() + 800,
                                       shroom.getYCenter());
        } else {
            d[1] = CrashGeom.distance2(shroom, target.getXCenter() + 800,
                                       target.getYCenter());
        }

        // if I'm above my target
        if (shroom.getY() < target.getY()) {
            d[2] = CrashGeom.distance2(target, shroom.getXCenter(), shroom
                                       .getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(shroom, target.getXCenter(), target
                                       .getYCenter() + 600);
        }

        float angle = CrashGeom.calculateAngle(target, shroom);
        if (d[1] < d[0] || d[2] < d[0]) {
            angle += Math.PI;
        }

        return angle;
    }
}
