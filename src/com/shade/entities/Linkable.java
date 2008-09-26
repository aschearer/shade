package com.shade.entities;

import com.shade.crash.Body;

/**
 * Linkables are bodies which form a doubly linked list.
 * 
 * This is the pattern used to implement the mushrooms-player following
 * behavior. Mushrooms should follow the player but stay a certain distance
 * away.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public abstract class Linkable extends Body {

    public Linkable prev, next;

    /**
     * Checks whether a linkable is over the edge of the screen and wraps it if
     * it is.
     */
    protected void testAndWrap() {
        if (getCenterX() <= 5) {
            shape.setCenterX(795);
        }
        if (getCenterX() > 795) {
            shape.setCenterX(5);
        }
        if (getCenterY() <= 5) {
            shape.setCenterY(595);
        }
        if (getCenterY() > 595) {
            shape.setCenterY(5);
        }
    }

}
