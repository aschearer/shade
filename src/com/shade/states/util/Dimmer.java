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
        timer += delta;
        if (color.a < targetAlpha && timer > 100) {
            color.a += ALPHA_INC;
            timer = 0;
        }
    }
    
    public void reset() {
        timer = 0;
        color.a = 0;
    }

}
