package com.shade.lighting;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

public class GlobalLight implements LightSource {


    private static final float TRANSITION_TIME = 1 / 7f;

    private final float transitionAngle;
    private final int secondsPerDay;
    private int timeOfDay;
    private float angle, depth;

    public GlobalLight(float depth, float angle, int duration) {
        this.depth = depth;
        this.angle = angle;
        secondsPerDay = duration;
        transitionAngle = (float) (2 * Math.PI / secondsPerDay);
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
        if(isDay())
        {
        g.setColor(new Color(0,0,0,1f));
        }
        else
        {
        	g.setColor(new Color(0,0,0,0.6f));
        }
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);

        //LightMask.disableStencil();
    }

    public void update(StateBasedGame game, int delta) {
        timeOfDay = (timeOfDay + delta) % secondsPerDay;
        if (isDay()) {

        }
        //System.out.println("timeOfDay "+timeOfDay);
        angle += transitionAngle * delta;
    }

    private boolean isDay() {
    	return (timeOfDay < 1f * secondsPerDay * (1f / 2 - TRANSITION_TIME));
    }

    public Shape castShadow(LuminousEntity e) {
        return e.castShadow(angle, depth);
    }

}
