package com.shade.controls.text;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Text which gradually fades out from a given color when displayed.
 * 
 * <ul>
 * <li>Displayed by default.</li>
 * <li>White by default.</li>
 * <li>Takes one second to fade-out.</li>
 * <li>No pause by default.</li>
 * </ul>
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class FadeOutText extends Text {

    private static final int DEFAULT_DURATION = 1000;
    private float alphaChange;
    private boolean active;
    private int timer;

    public FadeOutText(String message, Font f, float x, float y) {
        this(message, f, new Color(Color.white), x, y);
    }

    public FadeOutText(String message, Font f, Color c, float x, float y) {
        super(message, f, c, x, y);
        display(true);
        setDuration(DEFAULT_DURATION);
    }

    /**
     * Set how long it should take for the text to fade out.
     * 
     * @param duration in milliseconds.
     */
    public void setDuration(int duration) {
        alphaChange = (float) 1 / duration;
    }
    
    /**
     * Optionally set an amount of time to wait before displaying. 
     * @param duration
     */
    public void setPause(int duration) {
        timer = duration;
    }

    /**
     * Set to hide to start to fade-out the text. Set to display to immediately
     * show.
     */
    @Override
    public void display(boolean yes) {
        if (yes) {
            super.display(yes);
        }
        active = !yes;
    }

    @Override
    public void update(StateBasedGame game, int delta) {
        timer -= delta;
        if (timer > 0) {
            return;
        }
        if (active) {
            tweak(alphaChange * delta);
        }
    }
}
