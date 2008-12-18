package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;

/**
 * A meter which can go from zero to a hundred.
 *  + The control should be notified whenever a mushroom is collected. + The
 * control should + When the control reaches zero it will fire an event.
 * 
 * @author aas11
 * 
 */
public class MeterControl implements ControlSlice, MushroomCounter {

    private static final float WIDTH = 24;
    private static final float HEIGHT = 124;
    private static final Color BORDER = new Color(163, 183, 139);
    private static final Color ON = new Color(99, 125, 88);
    private static final Color OFF = new Color(163, 191, 95);

    private LuminousEntity target;
    private ControlListener listener;
    private ScoreControl scorecard;

    private float x, y;
    private float value, totalAmountToAdd, rateOfChange;

    public MeterControl(float x, float y) {
        this.x = x;
        this.y = y;
        value = 100;
        totalAmountToAdd = 0;
        rateOfChange = 1;
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
        g.setColor(OFF);
        g.fillRect(x, y, WIDTH, HEIGHT);

        g.setColor(ON);
        float adjustment = HEIGHT * (value / 100);
        g.fillRect(x, y + (HEIGHT - adjustment), WIDTH, adjustment);

        g.setColor(BORDER);
        g.drawRect(x, y, WIDTH, HEIGHT);

        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        if (value == 0) {
            listener.fire();
        }
        if (target != null && target.getLuminosity() > .6) {
            decrement(.1f);
        }
        
        if (totalAmountToAdd > 0) {
            value += .1f * rateOfChange;
            totalAmountToAdd -= .1f * rateOfChange;
        } else {
            rateOfChange = 1;
        }
        clamp();
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

    private void decrement(double amt) {
        value -= amt;
        clamp();
    }

    public void reset() {
        // TODO Auto-generated method stub

    }
}
