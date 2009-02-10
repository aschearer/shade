package com.shade.crash;

import com.crash.Body;

/**
 * A solid object which pushes back on intersection.
 *
 * All obstacles should implement this interface so that they can push the
 * player out of intersection.
 *
 * TODO some implementations of this class are less than ideal, see Dome.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface Repelable {

    /**
     * Push b away from this object such that the two are not intersecting.
     *
     * This is used when a collision occurs to push the player out of
     * intersection. It is also used to allow the player to push certain
     * obstacles around.
     *
     * @param b
     */
    public void repel(Body b);
    
}
