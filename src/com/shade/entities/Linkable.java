package com.shade.entities;

import java.util.Arrays;

import com.crash.Body;
import com.shade.crash.CrashGeom;
import com.shade.lighting.LuminousEntity;

/**
 * Linkables are bodies which form a doubly linked list.
 *
 * This is the pattern used to implement the mushrooms-player following
 * behavior. Mushrooms should follow the player but stay a certain distance
 * away.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public abstract class Linkable extends Body implements LuminousEntity {

    public Linkable prev, next;

    /**
     * Attach the object to the end of this linked list.
     *
     * @param l
     */
    protected void attach(Linkable l) {
        if (next == null) {
            next = l;
            l.prev = this;
            return;
        }
        Linkable head = next;
        while (head.next != null) {
            head = head.next;
        }
        head.next = l;
        l.prev = head;
    }

    /**
     * Remove this object from its linked list.
     */
    protected void detach() {
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        prev = null;
        next = null;
    }

    /**
     * Checks whether a linkable is over the edge of the screen and wraps it if
     * it is.
     */
    protected void testAndWrap() {
        if (getXCenter() <= 5) {
            shape.setCenterX(795);
        }
        if (getXCenter() > 795) {
            shape.setCenterX(5);
        }
        if (getYCenter() <= 5) {
            shape.setCenterY(595);
        }
        if (getYCenter() > 595) {
            shape.setCenterY(5);
        }
    }

    /**
     * Return true if this body and the target are further apart than the
     * threshold.
     *
     * @param target
     * @param threshold
     * @return
     */
    protected boolean overThreshold(Body target, float threshold) {
        float[] d = new float[3];

        d[0] = CrashGeom.distance2(target, this);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (getX() < target.getX()) {
            d[1] = CrashGeom
                   .distance2(target, getXCenter() + 800, getYCenter());
        } else {
            d[1] = CrashGeom.distance2(this, target.getXCenter() + 800, target
                                       .getYCenter());
        }

        // if I'm above my target
        if (getY() < prev.getY()) {
            d[2] = CrashGeom
                   .distance2(target, getXCenter(), getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(this, target.getXCenter(), target
                                       .getYCenter() + 600);
        }

        Arrays.sort(d);

        return (d[0] > threshold);
    }

}
