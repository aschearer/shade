package com.shade.controls.text;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

/**
 * An image which is rendered in a specific location.
 * 
 * <ul>
 * <li>Displayed by default.</li>
 * <li>No filter by default.</li>
 * </ul>
 *
 * @author Alex Schearer <aschearer@gmail.com>
 */
public class AnImage implements Animatable {
    
    private static final float MAX_ALPHA = 1;
    private static final float MIN_ALPHA = 0;
    
    private float x, y;
    private Image sprite;
    private Color filter;
    
    public AnImage(Image i, float x, float y) {
        this(i, new Color(Color.white), x, y);
    }
    
    public AnImage(Image i, Color c, float x, float y) {
        sprite = i;
        filter = c;
        this.x = x;
        this.y = y;
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(x, y, filter);
    }
    
    /**
     * Performs complex mathematical operations to ensure fidelty.
     */
    public void update(StateBasedGame game, int delta) {
        // nothing to update
    }

    /**
     * Text can be hidden, tricky text.
     * 
     * @param yes
     */
    public void display(boolean yes) {
        filter.a = (yes) ? MAX_ALPHA : MIN_ALPHA;
    }

    /* Some hooks for the children. */

    /**
     * Move the text over x, y amount.
     * 
     * @param x
     * @param y
     */
    protected void nudge(float x, float y) {
        x += x;
        y += y;
    }

    /**
     * Adjust the alpha ever so slightly.
     * 
     * @param c
     */
    protected void tweak(float alpha) {
        filter.a += alpha;
        clamp(filter);
    }

    /* For the sake of my sanity. */
    private void clamp(Color c) {
        if (c.a < MIN_ALPHA) {
            c.a = MIN_ALPHA;
        }
        if (c.a > MAX_ALPHA) {
            c.a = MAX_ALPHA;
        }
    }

}
