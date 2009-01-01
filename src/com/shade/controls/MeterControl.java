package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

/**
 * A meter which can go from zero to a hundred.
 *  + The control should be notified whenever a mushroom is collected. + The
 * control should + When the control reaches zero it will fire an event.
 * 
 * @author aas11
 * 
 */
public class MeterControl implements ControlSlice, MushroomCounter {

    private LuminousEntity target;
    private ControlListener listener;
    private ScoreControl scorecard;

    private float x, y;
    private float value, totalAmountToAdd, rateOfChange;
    private static Image front, back, danger, current;
    private float[] damages = { .1f, .175f, .3f };
    private int timeInSun;
    private int dangerTimer;
    
    static {
        try {
            front = new Image("states/ingame/meter-front.png");
            back = new Image("states/ingame/meter-back.png");
            danger = new Image("states/ingame/meter-danger.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public MeterControl(float x, float y) throws SlickException {
        this.x = x;
        this.y = y;
        value = 100;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        initResources();
        current = back;
    }

    private void initResources() throws SlickException {
        
    }

    public void track(LuminousEntity e) {
        target = e;
    }

    public void register(ControlListener c) {
        listener = c;
    }

    public void pass(ScoreControl card) {
        scorecard = card;
    }

    public void render(StateBasedGame game, Graphics g) {
        current.draw(x, y);
        
        float w = front.getWidth();
        float h = front.getHeight();
        float adjustment = h - (h * (value / 100));
        front.draw(x, y + adjustment, x + w, y + h, 0, adjustment, w, h);
    }

    public void update(StateBasedGame game, int delta) {
        if (value == 0) {
            listener.fire();
        }
        if (target != null && target.getLuminosity() > MasterState.SHADOW_THRESHOLD) {
            decrement(delta);
        } else {
            timeInSun = 0;
        }
        
        if (totalAmountToAdd > 0) {
            value += .1f * rateOfChange;
            totalAmountToAdd -= .1f * rateOfChange;
        } else {
            rateOfChange = 1;
        }
        clamp();
        
        if (value < 40) {
            dangerTimer += delta;
            if (dangerTimer > 100) {
                dangerTimer = 0;
                current = (current == danger) ? back : danger;
            }
        }
    }
    
    public void awardBonus() {
        totalAmountToAdd += 20;
    }

    public void onCollect(Mushroom shroomie) {
        valueMushroom(shroomie);
        if (totalAmountToAdd > 0) {
            rateOfChange++;
        }
    }

    private void clamp() {
        if (value < 0) {
            value = 0;
        }
        if (value > 100) {
            scorecard.add(value - 100);
            value = 100;
        }
    }

    private void valueMushroom(Mushroom shroomie) {
        totalAmountToAdd += shroomie.getValue() * 4;
    }

    private void decrement(int delta) {
        timeInSun += delta;
        float damage = damages[0];
        if (timeInSun > 1000) {
           damage = damages[1];
        }
        if (timeInSun > 4000) {
           damage = damages[2];
        }

        value -= damage;
        clamp();
    }

    public void reset() {
        value = 100;
        totalAmountToAdd = 0;
        rateOfChange = 1;
    }
}
