package com.shade.controls.text;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

/**
 * Text which will be rendered on screen, boring.
 * 
 * <ul>
 * <li>Displayed by default.</li>
 * <li>White by default.</li>
 * </ul>
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class Text implements Animatable {

    private static final float MAX_ALPHA = 1;
    private static final float MIN_ALPHA = 0;

    private float x, y;
    private String content;
    private Font font;
    private Color color;

    /**
     * Create a new text to render at the given point.
     * 
     * @param message
     * @param f
     * @param x
     * @param y
     */
    public Text(String message, Font f, float x, float y) {
        this(message, f, new Color(Color.white), x, y);
    }

    /**
     * Create a new text to render at the given point in the given color.
     * 
     * @param message
     * @param f
     * @param c
     * @param x
     * @param y
     */
    public Text(String message, Font f, Color c, float x, float y) {
        content = message;
        font = f;
        this.x = x;
        this.y = y;
        color = c;

    }

    /**
     * Renders text to the screen.
     */
    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, content, color);
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
        color.a = (yes) ? MAX_ALPHA : MIN_ALPHA;
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
        color.a += alpha;
        clamp(color);
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
