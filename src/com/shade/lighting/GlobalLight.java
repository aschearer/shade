package com.shade.lighting;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

public class GlobalLight implements LightSource {


    private static final float TRANSITION_TIME = 1 / 7f;
    private static final float TRANSITION_ANGLE = .0001f;
    private static final int SECONDS_PER_DAY = (int) Math.ceil(Math.PI * 2
            / TRANSITION_ANGLE);

    private int timeOfDay;
    private float angle, depth;

    public GlobalLight(float depth, float angle) {
        this.depth = depth;
        this.angle = angle;
    }

    public void render(StateBasedGame game, Graphics g,
                       LuminousEntity... entities) {
        LightMask.resetStencil();
        g.setColor(Color.black);
        for (LuminousEntity entity : entities) {
            Shape s = entity.castShadow(angle, depth);
            if (s != null) {
                g.fill(s);
            }
        }
        LightMask.keepStencil();

        GameContainer c = game.getContainer();
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);

        //LightMask.disableStencil();
    }

    public void update(StateBasedGame game, int delta) {
        timeOfDay = (timeOfDay + delta) % SECONDS_PER_DAY;
        if (dayOrNight()) {

        }
        angle += .005f;
    }

    private boolean dayOrNight() {
        return (timeOfDay > 1f * SECONDS_PER_DAY * (1 / 2 - TRANSITION_TIME));
    }

    public Shape castShadow(LuminousEntity e) {
        return e.castShadow(angle, depth);
    }

}
