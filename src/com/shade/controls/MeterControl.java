package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.entities.Mushroom;

public class MeterControl implements MushroomCounter, Animatable {

    private static final float BASE_INCREMENT = .05f;
    private static final float BASE_MULTIPLE = 3;
    private static final float WIDTH = 24;
    private static final float HEIGHT = 124;
    private static final Color BORDER = new Color(163, 183, 139);
    private static final Color ON = new Color(99, 125, 88);
    private static final Color OFF = new Color(163, 191, 95);

    private float x, y;
    private float value, max;

    public MeterControl(float x, float y, float value, float max) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.max = max;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isFull() {
        return value == max;
    }

    public void onCollect(Mushroom shroomie) {
        while (shroomie.next != null) {
            value += shroomie.getSize() * BASE_MULTIPLE;
            shroomie = (Mushroom) shroomie.next;
        }
        value += shroomie.getSize() * BASE_MULTIPLE;
    }

    public void decrement() {
        value += -BASE_INCREMENT;
        clamp();
    }

    public void render(Graphics g) {
        g.setColor(OFF);
        g.fillRect(x, y, WIDTH, HEIGHT);

        g.setColor(ON);
        float adjustment = HEIGHT * (value / max);
        g.fillRect(x, y + (HEIGHT - adjustment), WIDTH, adjustment);

        g.setColor(BORDER);
        g.drawRect(x, y, WIDTH, HEIGHT);

        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        clamp();
    }
    

    private void clamp() {
        if (value < 0) {
            value = 0;
        }
        if (value > max) {
            value = max;
        }
    }
}
