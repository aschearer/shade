package com.shade.base;

import org.newdawn.slick.state.StateBasedGame;

/**
 * A collection of objects which can be updated; This provides a mechanism for
 * adding and removing entities dynamically to a game.
 *
 * @author Alex Schearer <aschearer@gmail.com>
 */
public interface Level<T extends Entity> {

    /**
     * This should call Entity.addToLevel.
     *
     * @param e
     */
    public void add(T e);

    /**
     * This should call Entity.removeFromLevel.
     *
     * @param e
     */
    public void remove(T e);

    /**
     * Return an array of entities which perform the given role.
     * @param role
     * @return
     */
    public Object[] getEntitiesByRole(int role);

    /**
     * For each entity call Entity.removeFromLevel.
     */
    public void clear();

    public void update(StateBasedGame game, int delta);

    public T[] toArray(T[] a);

}
