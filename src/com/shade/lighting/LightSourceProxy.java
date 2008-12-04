package com.shade.lighting;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A collection of lights wrapped up behind a single interface.
 *
 * Create this alongside a LightMask. Pass this to the mask for rendering and
 * call update here. This class acts as a model for lights.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LightSourceProxy implements LightSource {

    private LinkedList<LightSource> lights;

    public LightSourceProxy() {
        lights = new LinkedList<LightSource>();
    }

    public void add(LightSource l) {
        lights.add(l);
    }

    public void render(StateBasedGame game, Graphics g,
                       LuminousEntity... entities) {
        for (LightSource light : lights) {
            light.render(game, g, entities);
        }
    }

    public void update(StateBasedGame game, int delta) {
        for (LightSource light : lights) {
            light.update(game, delta);
        }
    }

}
