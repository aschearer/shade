package com.shade.lighting;

import org.newdawn.slick.geom.Shape;

import com.shade.base.Entity;

/**
 * An entity which lives in a world of light.
 *
 * The goal of this class is to stretch the word "luminous" as much as possible.
 * It's also to provide an interface for any object which can cast or be under a
 * shadow.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface LuminousEntity extends Entity, Comparable<LuminousEntity> {

    /**
     * The depth of this entity; used to calculate whether shadows should be
     * cast on or under it.
     *
     * @return
     */
    public int getZIndex();

    /**
     * Yes, luminosity... a word not even Eclipse recognizes.
     *
     * Use this to determine whether the entity is in the light or shade.
     *
     * @return
     */
    public float getLuminosity();

    /**
     * Just how much in the shade is this entity.
     *
     * @param l
     * @return
     */
    public void setLuminosity(float l);

    /**
     * Generate and return a shadow cast by a light source from the given
     * direction and with the given depth.
     *
     * @param direction
     * @param depth
     * @return
     */
    public Shape castShadow(float direction, float depth);

}
