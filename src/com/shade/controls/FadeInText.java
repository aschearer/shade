package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class FadeInText implements Animatable {

    private enum Status {
        INACTIVE, ACTIVE
    };

    private String text;
    private TrueTypeFont font;
    private int x, y;
    private Color color;
    private int delay, timer;
    private Status status;

    public FadeInText(String text, TrueTypeFont font, int x, int y, int delay) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.delay = delay;
        color = new Color(Color.white);
        color.a = 0;
        status = Status.INACTIVE;
    }

    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, text, color);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (delay < timer) {
            status = Status.ACTIVE;
        }
        if (status != Status.ACTIVE) {
            return;
        }
        if (timer > 100 && color.a < 1) {
            timer = 0;
            color.a += .05f;
        }
    }
}
