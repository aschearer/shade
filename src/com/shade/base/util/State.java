package com.shade.base.util;

import com.shade.base.Animatable;
import com.shade.base.Entity;

/**
 * A proxy for the entity interface; This allows you to split an entity into a
 * set of discrete classes.
 *
 * This is useful when you want to implement a complex state based entity, for
 * instance an entity with sophisticated AI.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 *
 */
public interface State extends Animatable {

    /**
     * Returns true if the object equals this state.
     *
     * Generally, have each state map to an enum and return true if o equals the
     * enum.
     */
    public boolean isNamed(Object o);

    /**
     * Called whenever the state is transitioned to; perform any necessary set
     * up here.
     */
    public void enter();

    public int getRole();

    /**
     * Completes the Entity interface...
     *
     * @param obstacle
     */
    public void onCollision(Entity obstacle);

}