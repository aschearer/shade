package com.shade.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import com.shade.crash.Body;
import com.shade.util.Geom;

public abstract class ShadowCaster extends Body {

    /**
     * How tall this body is.
     */
    protected float depth;
    /**
     * The body's shadow.
     */
    protected Shape shadow;

    /**
     * Calculates this bodies shadow.
     * 
     * This should be called whenever the direction of the sun changes. It is
     * assumed that this will only be called after the body is initialized.
     * 
     * @param direction The angle of the sun in radians.
     */
    public void castShadow(float direction) {
        shadow = shape;
        
        for (int i = 0; i < depth; i++) {
            Vector2f d = Geom.calculateVector(10 * i, direction);
            Transform t = Transform.createTranslateTransform(d.x, d.y);
            Shape[] union = shadow.union(shape.transform(t));
            shadow = union[0];
        }
    }

    /**
     * Render this body's shadow.
     * 
     * @param g
     */
    protected void renderShadow(Graphics g) {
        Color c = g.getColor();
        Color shade = Color.black;
        shade.a = .5f;
        g.setColor(shade);
        g.fill(shadow);
        g.setColor(c);
    }
}
