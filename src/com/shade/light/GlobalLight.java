package com.shade.light;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.shadows.ShadowLevel.DayLightStatus;

public class GlobalLight extends InfiniteLight implements LightSource {
	public static final float TRANSITION_TIME = 1f / 7;
	public static final float MAX_SHADOW = 0.8f;
	public static final float SUN_ANGLE_INCREMENT = 0.0005f;
	public static final int SECONDS_PER_DAY = (int) Math.ceil(Math.PI * 2
			/ SUN_ANGLE_INCREMENT);

	int time;
	Color light;
	int radius;

	public GlobalLight(int x, int y, float intense, int rad, int height) {
		super(x, y, intense, rad, height);
		intense = MAX_SHADOW;
		radius = rad;
		light = new Color(0, 0, 0, MAX_SHADOW);
		time = 0;
	}

	public void update(StateBasedGame game, int delta) {
		time += delta;
		int timeofday = time % SECONDS_PER_DAY;
		// is it day or night?
		if (timeofday > 1.0 * SECONDS_PER_DAY * (1f / 2 - TRANSITION_TIME)) {
			float factor = MAX_SHADOW;
			float colorizer = 0;
			float colorizeg = 0;
			float colorizeb = 0;
			float mathstuff = ((timeofday - SECONDS_PER_DAY / 2f)
					/ (SECONDS_PER_DAY * TRANSITION_TIME) + 1);
			if (timeofday < 1.0 * SECONDS_PER_DAY / 2) {
				factor = (float) 1.0
						* MAX_SHADOW
						* mathstuff + 0.3f;
				colorizer = 0.2f * mathstuff;
				colorizeg = 0.1f * mathstuff;

			}
			if (timeofday > 1.0 * SECONDS_PER_DAY * (1 - TRANSITION_TIME)) {
				factor = MAX_SHADOW * (SECONDS_PER_DAY - timeofday)
						/ (SECONDS_PER_DAY * TRANSITION_TIME);
				colorizer = 0.1f * (float) Math.abs(Math.cos(Math.PI
						/ 2
						* ((timeofday - SECONDS_PER_DAY / 2f)
								/ (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
				colorizeg = 0.1f * (float) Math.abs(Math.cos(Math.PI
						/ 2
						* ((timeofday - SECONDS_PER_DAY / 2f)
								/ (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
				colorizeb = 0.05f * (float) Math.abs(Math.cos(Math.PI
						/ 2
						* ((timeofday - SECONDS_PER_DAY / 2f)
								/ (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
			}
			light = new Color(colorizer, colorizeg, colorizeb, factor);
			//light = new Color(0,0,0, factor);
		}

		
		angle += delta*SUN_ANGLE_INCREMENT;
		double newx = myCenterx + radius
				* Math.cos(angle);
		double newy = myCentery + radius
				* Math.sin(angle);
		myPosition.set((float) newx, (float) newy);
	}

	public void renderLight(Graphics g, int width, int height) {
		g.setColor(light);
		//g.setColor(new Color(1.0f,1.0f,0,0));
		g.fill(new Rectangle(0, 0, width, height));

	}
	
	public float getIntensity(){
		return light.a;
	}

}
