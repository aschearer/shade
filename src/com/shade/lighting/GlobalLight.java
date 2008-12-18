package com.shade.lighting;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.DayPhaseTimer;

public class GlobalLight implements LightSource {

    private final float transitionAngle;
    private final int secondsPerDay;
    private int timeOfDay;
    private float angle, depth;
    private DayPhaseTimer timer;

    public GlobalLight(float depth, float angle, int duration, DayPhaseTimer t) {
        this.depth = depth;
        this.angle = angle;
        secondsPerDay = duration;
        transitionAngle = (float) (2 * Math.PI / secondsPerDay);
        timer = t;
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
        if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.DAY)
        {
        g.setColor(new Color(0,0,0,1f));
        }
        else
        {
        	float factor = (1-timer.timeLeft())*0.4f;
        	if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.NIGHT) factor = 0;
        	if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.DAWN) factor = timer.timeLeft()*0.4f;
        	g.setColor(new Color(0,0,0,0.6f+factor));
        }
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);

        //LightMask.disableStencil();
    }

    public void update(StateBasedGame game, int delta) {
        timeOfDay = (timeOfDay + delta) % secondsPerDay;
        //System.out.println("timeOfDay "+timeOfDay);
        angle += transitionAngle * delta;
    }


    public Shape castShadow(LuminousEntity e) {
        return e.castShadow(angle, depth);
    }

}
