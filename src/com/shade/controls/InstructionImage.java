package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

/* Some image which hides, displays itself, pauses, then hides again. */
public class InstructionImage implements Animatable {

    private enum State {
        HIDDEN, ACTIVE, FINISHED
    };

    private float x, y;
    private int hideTimer, showTimer, alphaTimer;
    private int hideTime, showTime;
    private State state;
    private Image sprite;
    private Color filter;

    public InstructionImage(float x, float y, Image sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        state = State.HIDDEN;
        filter = new Color(Color.white);
        filter.a = 0;
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
        sprite.draw(x, y, filter);
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
            if (alphaTimer > 100 && filter.a < 1) {
                alphaTimer = 0;
                filter.a += .05f;
            }
            if (showTimer > showTime) {
                state = State.FINISHED;
            }
        }
        if (state == State.FINISHED && filter.a > 0) {
            alphaTimer += delta;
            if (alphaTimer > 100) {
                alphaTimer = 0;
                filter.a -= .1f;
            }
        }
    }

}
