package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class FadeInImage implements Animatable {

    private enum Status {
        INACTIVE, ACTIVE
    };

    private Image sprite;
    private Color filter;
    private int x, y, width, height;
    private int delay, timer;
    private Status status;

    public FadeInImage(Image s, int x, int y, int delay) {
        this(s, x, y, s.getWidth(), s.getHeight(), delay);
    }
    
    public FadeInImage(Image s, int x, int y, int w, int h, int delay) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.delay = delay;
        status = Status.INACTIVE;
        sprite = s;
        filter = new Color(Color.white);
        filter.a = 0;
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(x, y, width, height, filter);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (delay < timer) {
            status = Status.ACTIVE;
        }
        if (status != Status.ACTIVE) {
            return;
        }
        if (timer > 100 && filter.a < 1) {
            timer = 0;
            filter.a += .05f;
        }
    }
}
