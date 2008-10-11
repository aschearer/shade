package com.shade.light;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Defines an object which will produce light.
 * When combined with a ShadowCaster, it produces
 * dynamic shadows. 
 * @author User
 *
 */
public interface LightSource {
	Vector2f getPosition();
	float getIntensity();
	void update(StateBasedGame game, int delta);
}
