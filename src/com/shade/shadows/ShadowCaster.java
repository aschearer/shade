package com.shade.shadows;

import org.newdawn.slick.geom.Shape;

import com.shade.base.Entity;

/**
 * Defines an object which can cast a shadow.
 * 
 * Shadows are collected and finally merged to form a the shadowscape. Shadows
 * are updated periodically as the sun moves. Finally, shadow casting objects
 * are in order according to their z-index. This means that "taller" entities
 * will be rendered on top of "shorter" ones.
 * 
 * @author Alexander Schearer <aas11@duke.edu>
 */
public interface ShadowCaster extends Entity {

    /**
     * Returns the entity's depth, or how tall it is, in the 3rd dimension.
     * 
     * @return
     */
    public int getZIndex();

    /**
     * Calculates this bodies shadow.
     * 
     * This should be called whenever the direction of the sun changes. It is
     * assumed that this will only be called after the body is initialized.
     * 
     * @param direction
     *            The angle of the sun in radians.
     * @param depth
     *            TODO
     * @return the object's shadow.
     */
    public Shape castShadow(float direction, float depth);
}
