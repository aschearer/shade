package com.shade.lighting;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Defines an object which will produce light; When combined with a
 * LuminousEntity it produces dynamic shadows.
 *
 * @author JJ Jou <j.j@duke.edu>
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public interface LightSource {

    /**
     * Render shadows for a set of entities.
     *
     * @param game
     * @param g
     * @param entities
     */
    public void render(StateBasedGame game, Graphics g,
                       LuminousEntity... entities);

    /**
     * Change the position or depth of the light as necessary.
     *
     * @param game
     * @param delta
     */
    public void update(StateBasedGame game, int delta);

}
