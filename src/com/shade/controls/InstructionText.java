package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

/* Some text which hides, displays itself, pauses, then hides again. */
public class InstructionText implements Animatable {

    private enum State {
        HIDDEN, ACTIVE, FINISHED
    };

    private float x, y;
    private String message;
    private TrueTypeFont font;
    private int hideTimer, showTimer, alphaTimer;
    private int hideTime, showTime;
    private State state;
    private Color color;

    public InstructionText(float x, float y, String message, TrueTypeFont font) {
        this.x = x;
        this.y = y;
        this.message = message;
        this.font = font;
        state = State.HIDDEN;
        color = new Color(Color.white);
        color.a = 0;
    }

    /* How long to stay invisible for. */
    public void setTimer(int time) {
        hideTime = time;
    }

    /* How long to remain visible for. */
    public void setDuration(int time) {
        showTime = time;
    }

    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, message, color);
    }

    public void update(StateBasedGame game, int delta) {
        if (state == State.HIDDEN) {
            hideTimer += delta;
            if (hideTimer > hideTime) {
                state = State.ACTIVE;
            }
        }
        if (state == State.ACTIVE) {
            showTimer += delta;
            alphaTimer += delta;
            if (alphaTimer > 100 && color.a < 1) {
                alphaTimer = 0;
                color.a += .05f;
            }
            if (showTimer > showTime) {
                state = State.FINISHED;
            }
        }
        if (state == State.FINISHED && color.a > 0) {
            alphaTimer += delta;
            if (alphaTimer > 100) {
                alphaTimer = 0;
                color.a -= .1f;
            }
        }
    }
    
    public boolean finished() {
        return state == State.FINISHED;
    }

}
