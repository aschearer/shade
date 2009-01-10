package com.shade.controls.text;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A simple composite of the fade in/out text which lets you create text which,
 * yes, fades in and out.
 * 
 * <ul>
 * <li>Hidden by default.</li>
 * <li>White by default.</li>
 * <li>Takes one second to fade-in.</li>
 * <li>Takes one second to fade-out.</li>
 * <li>No pause by default.</li>
 * </ul>
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class AlphaText extends Text {

    private enum State {
        FADEIN, FADEOUT;
    };

    private State state;
    private FadeInText fadein;
    private FadeOutText fadeout;

    public AlphaText(String message, Font f, float x, float y) {
        this(message, f, new Color(Color.white), x, y);
    }

    public AlphaText(String message, Font f, Color c, float x, float y) {
        super(message, f, c, x, y);
        fadein = new FadeInText(message, f, c, x, y);
        fadeout = new FadeOutText(message, f, c, x, y);
    }

    /**
     * Set how long it should take for the text to fade in/out.
     * @param duration
     */
    public void setDuration(int duration) {
        fadein.setDuration(duration);
        fadeout.setDuration(duration);
    }
    
    /**
     * Set the pause time for fade-in and fade-out.
     * @param in
     * @param out
     */
    public void setPause(int in, int out) {
        fadein.setPause(in);
        fadeout.setPause(out);
    }

    /**
     * Set display in order to fade in the text, set hidden in order to fade out
     * the text.
     */
    @Override
    public void display(boolean yes) {
        if (yes) {
            state = State.FADEIN;
            fadein.display(yes);
        } else {
            state = State.FADEOUT;
            fadeout.display(yes);
        }
    }

    @Override
    public void render(StateBasedGame game, Graphics g) {
        if (state == State.FADEIN) {
            fadein.render(game, g);
        }
        if (state == State.FADEOUT) {
            fadeout.render(game, g);
        }
    }

    @Override
    public void update(StateBasedGame game, int delta) {
        if (state == State.FADEIN) {
            fadein.update(game, delta);
        }
        if (state == State.FADEOUT) {
            fadeout.update(game, delta);
        }
    }
}
