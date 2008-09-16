package com.shade.shadows;

import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;

public class Shadowscape {
    
    private LinkedList<Shape> shadows;
    
    public Shadowscape(ZBuffer buffer, float direction) {
        shadows = new LinkedList<Shape>();
        for (ShadowCaster c : buffer) {
            shadows.add(c.castShadow(direction));
        }
    }

    /**
     * Draw the shadowscape.
     * 
     * @param g
     */
    public void render(Graphics g) {
        Color shade = Color.black;
        shade.a = .5f;
        g.setColor(shade);
        for (Shape s : shadows) {
            g.fill(s);
        }
        g.setColor(Color.white);
    }

}
