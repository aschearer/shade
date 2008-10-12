package com.shade.light;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

public class InfiniteLight extends AbstractLight {

    public InfiniteLight(int x, int y, float intense) {
        super(x, y, intense);
        myCenterx = x;
        myCentery = y;
        myPosition = new Vector2f(x + 10, y);
        angle = 0;
        intensity = intense;
    }

    public float getCastLength() {
        return 10;
    }

    public void update(StateBasedGame game, int delta) {
        angle += delta;
        double newx = myCenterx + 100 * Math.cos(4.0 * angle / 30000 * Math.PI);
        double newy = myCentery + 100 * Math.sin(4.0 * angle / 30000 * Math.PI);
        // System.out.println("Moving light to "+newx+", "+newy);
        myPosition.set((float) newx, (float) newy);
    }

    public void renderLight(Graphics g, int width, int height) {
        g.setColor(new Color(0, 0, 0, intensity));
        g.fill(new Rectangle(0, 0, width, height));

    }

}
