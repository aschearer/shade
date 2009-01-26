package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

/* Some text which hides, displays itself, pauses, then hides again. */
public class InstructionText implements Animatable {

    private enum State {
        OFF, FADEIN, FADEOUT
    };

    private float x, y;
    private String message;
    private TrueTypeFont font;
    private int hideTimer, showTimer, alphaTimer;
    private int hideTime;
    private State state;
    private Color color;

    public InstructionText(float x, float y, String message, TrueTypeFont font) {
        this.x = x;
        this.y = y;
        this.message = message;
        this.font = font;
        state = State.OFF;
        color = new Color(Color.white);
        color.a = 0;
    }

    /* How long to stay invisible for. */
    public void setTimer(int time) {
        hideTime = time;
    }
    
    public void activate() {
        state = State.FADEIN;
    }
    
    public void deactivate() {
        state = State.FADEOUT;
    }

    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, message, color);
    }

    public void update(StateBasedGame game, int delta) {
        if (state == State.FADEIN) {
            hideTimer += delta;
            if (hideTime < hideTime) {
                return;
            }
            showTimer += delta;
            alphaTimer += delta;
            if (alphaTimer > 100 && color.a < 1) {
                alphaTimer = 0;
                color.a += .05f;
            }
        }
        if (state == State.FADEOUT && color.a > 0) {
            alphaTimer += delta;
            if (alphaTimer > 100) {
                alphaTimer = 0;
                color.a -= .1f;
            }
        }
    }
    
    public void reset() {
        state = State.OFF;
        color.a = 0;
    }

}
