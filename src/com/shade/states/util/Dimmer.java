package com.shade.states.util;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class Dimmer implements Animatable {
    
    private static final float ALPHA_INC = .05f;
    private Color color;
    private float targetAlpha;
    private int timer;
    private boolean reversed;
    private boolean running;
    
    public Dimmer(float target) {
        color = new Color(Color.black);
        color.a = 0;
        targetAlpha = target;
    }

    public void render(StateBasedGame game, Graphics g) {
        g.setColor(color);
        GameContainer c = game.getContainer();
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        if (!running) {
            return;
        }
        timer += delta;
        if (!reversed && color.a < targetAlpha && timer > 100) {
            color.a += ALPHA_INC;
            timer = 0;
        }
        if (reversed && color.a >= 0 && timer > 100) {
            color.a -= ALPHA_INC;
            timer = 0;
        }
        
        clamp();
    }
    
    private void clamp() {
        if (!reversed && color.a > targetAlpha) {
            color.a = targetAlpha;
        }
        if (reversed && color.a < 0) {
            color. a = 0;
        }
    }

    public boolean finished() {
        return color.a == targetAlpha;
    }
    
    public void reverse() {
        reversed = true;
    }
    
    public void reset() {
        timer = 0;
        color.a = 0;
        reversed = false;
    }
    
    public void rewind() {
        reversed = !reversed;
    }

    public boolean reversed() {
        return reversed;
    }

    public void fastforward() {
        timer = 0;
        color.a = targetAlpha;
        reversed = true;
    }
    
    public void run() {
        running = true;
    }
    
    public void stop() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }

}
