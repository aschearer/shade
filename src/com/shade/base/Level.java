package com.shade.base;

/**
 * A collection of entities, this provides a means for adding and removing
 * entities dynamically.
 * 
 * @author Alex Schearer <aschearer@gmail.com>
 */
public interface Level extends Animatable {

    /**
     * This should call Entity.addToLevel.
     * @param e
     */
    public void add(Entity e);

    /**
     * This should call Entity.removeFromLevel.
     * @param e
     */
    public void remove(Entity e);

    /**
     * For each entity call Entity.removeFromLevel.
     */
    public void clear();

}
