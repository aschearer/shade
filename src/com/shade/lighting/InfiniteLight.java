package com.shade.lightrender;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

public class InfiniteLight extends AbstractLight {
	
	public float length;
	public int radius;
	
    public InfiniteLight(int x, int y, float intense, int radius, float length) {
        super(x, y, intense);
        myCenterx = x;
        myCentery = y;
        myPosition = new Vector2f(x + 10, y);
        angle = 0;
        intensity = intense;
        this.length = length;
        this.radius = radius;
    }

    public float getCastLength() {
        return length;
    }

    public void update(StateBasedGame game, int delta) {
		angle += delta*0.0003;
		double newx = myCenterx + radius
				* Math.cos(angle);
		double newy = myCentery + radius
				* Math.sin(angle);
		myPosition.set((float) newx, (float) newy);
    }

    public void renderLight(Graphics g, int width, int height) {
        g.setColor(new Color(0, 0, 0, intensity));
        g.fill(new Rectangle(0, 0, width, height));

    }

}
