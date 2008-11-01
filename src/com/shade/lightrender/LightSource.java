package com.shade.lightrender;

import org.newdawn.slick.Graphics;
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
	float getShadowIntensity();
	float getCastLength();
	void update(StateBasedGame game, int delta);
	/**
	 * a method that tells the light to render what it would like to
	 * as of now the shadow information is kept in the light map.
	 * TODO: Refactor this correctly.
	 * @param g
	 */
	void renderLight(Graphics g, int width, int height);
}
