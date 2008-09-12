package com.shade.crash;

import org.newdawn.slick.geom.Shape;

/** A collection of static methods for intersection testing. */
public class Collider {

    /**
     * Check each obstacle for intersection with the subject and alert them (by
     * calling Body.collide) if an intersection is found.
     */
    public static void testAndAlert(Body subject, Iterable<Body> obstacles) {
        for (Body obstacle : obstacles) {
            if (!subject.equals(obstacle) && intersecting(subject, obstacle)) {
                subject.onCollision(obstacle);
                obstacle.onCollision(subject);
            }
        }
    }

    /**
     * Return whether two bodies are intersecting. This should perform an
     * efficient bounds check before performing an expensive primitive check.
     */
    public static boolean intersecting(Body one, Body two) {
        return (checkBounds(one, two) && checkPrimitives(one.shape, two.shape));
    }

    /**
     * This is a cheap bounding circle check. It simply compares the distance
     * between the body's centers and the bounding circle's radii.
     */
    private static boolean checkBounds(Body one, Body two) {
        float x = (float) Math.pow(one.getCenterX() - two.getCenterX(), 2);
        float y = (float) Math.pow(one.getCenterY() - two.getCenterY(), 2);

        float r1 = one.shape.getBoundingCircleRadius();
        float r2 = two.shape.getBoundingCircleRadius();
        float r = (float) Math.pow(r1 + r2, 2);

        return ((x + y) <= r);
    }

    /**
     * Let the Slick shape class do the heavy lifting, this returns whether the
     * two primitives occupy the same space. Don't call this unell there's
     * reason to believe intersection exists.
     */
    private static boolean checkPrimitives(Shape one, Shape two) {
        return one.intersects(two);
    }
}
