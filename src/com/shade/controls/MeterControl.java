package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.Player;
import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

/**
 * A meter which can go from zero to a hundred. + The control should be notified
 * whenever a mushroom is collected. + The control should + When the control
 * reaches zero it will fire an event.
 * 
 * @author aas11
 * 
 */
public class MeterControl implements ControlSlice, MushroomCounter {

    public static final float BASE_DAMAGE = 0.02f;
    public static final float BASE_EXPONENT = 1.0005f;
    public static final float GOLD_SCORE_MULTIPLIER = 40;
    public static final float SCORE_MULTIPLIER = 10;
    public static final float HEALTH_MULTIPLIER = 1f;
    public static final float BAR_MAX = 40f;
    public static final float BASE_RECHARGE = BAR_MAX / 4000f; // max / 2 * sec
    public static final float BONUS_SCALE = 1.5f;

    private Player target;
    private ControlListener listener;
    private ScoreControl scorecard;

    private float x, y;
    private float value, totalAmountToAdd, rateOfChange;
    private float totalDecrement;
    private float totalTimeInSun;
    private static Image front, back, danger, current;
    private int timeInSun;
    // bonus life
    private float bonusMeter;
    private int dangerTimer;

    // metrics - we should get rid of these later
    private float totalTimeInShade, totalTimeStanding, totalTimeRunning;
    private int multiplier;

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
        value = 0;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        multiplier = 1;
        initResources();
        current = back;
    }

    private void initResources() throws SlickException {

    }

    public void track(LuminousEntity e) {
        target = (Player) e;
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
        float adjustment = h - (h * (value / BAR_MAX));
        front.draw(x, y + adjustment, x + w, y + h, 0, adjustment, w, h);
    }

    public void update(StateBasedGame game, int delta) {
        if (bonusMeter > 0) {
            scorecard.add(1);
            bonusMeter--;
        }
        
        /**
         * TODO: DELETE THIS LATER.
         * 
         */
        if (!isMoving(target))
            totalTimeStanding += delta;
        else
            totalTimeRunning += delta;
        /*
        if (target.isStunned()) {
            if (value < BAR_MAX / 2) {
                value += BASE_RECHARGE * delta;
            }
            totalAmountToAdd = 0;
            timeInSun = 0;
            return;
        }*/
        if (value == 0) {
            // listener.fire(this);
            target.stun();
            totalAmountToAdd = BAR_MAX/2;
        }
        // System.out.println(value);
        // if (value >= BAR_MAX&&bonusMeter<1) {
        // // System.out.println("wowza");
        // // not sure why this isn't player specific right now. It wil be form
        // // now on.
        // // TODO: if this shold go somewhere else tell me!
        // target.setSpeed(Player.MAX_SPEED * BONUS_SCALE);
        // target.sparkle();
        // bonusMeter = BAR_MAX/35;
        // } if(bonusMeter<1) {
        // target.unsparkle();
        // if (target.getSmokeCount() < timeInSun) {
        // target.setSpeed((float) Math.max(0, value / BAR_MAX)
        // * (Player.MAX_SPEED - Player.MIN_SPEED)
        // + Player.MIN_SPEED);
        // }
        // }
        // TODO: move this somwhere
        // int scale = 60;
        //System.out.println(target.getLuminosity());
        if (target != null
                && target.getLuminosity() > MasterState.SHADOW_THRESHOLD*8) {
        	           decrement(delta);

        } else if (value < BAR_MAX / 2 && !isMoving(target)) {
            timeInSun = 0;
            totalTimeInShade += delta;
            value += BASE_DAMAGE * 2;
            // timeInSun = Math.max(timeInSun - delta, 0);
            // if (target.getXVelocity() + target.getXVelocity() == 0)
            // timeInSun *= 0.8;
        }

        if (totalAmountToAdd > 0) {
            value += .1f * rateOfChange;
            totalAmountToAdd -= .1f * rateOfChange;
        } else {
            rateOfChange = 1;
        }

        clamp();

        target.setSpeed(Player.MIN_SPEED + (value / BAR_MAX)
                * (Player.MAX_SPEED - Player.MIN_SPEED));
    }

    private boolean isMoving(Player p) {
        return !(p.getXVelocity() == 0 && p.getYVelocity() == 0);
    }

    public void awardBonus() {
        totalAmountToAdd += 20;
    }

    public void onCollect(Mushroom shroomie) {
        if (target.isStunned()) {
            return;
        }
        valueMushroom(shroomie);
        if (totalAmountToAdd > 0) {
            // rateOfChange++;
            rateOfChange = 3;
        }
    }

    private void clamp() {
        if (value < 0) {
            value = 0;
        }
        if ((int) value > BAR_MAX) {
            bonusMeter += value - BAR_MAX;
            value = BAR_MAX;
        }
    }

    private void valueMushroom(Mushroom shroomie) {
        totalAmountToAdd += shroomie.getValue() * HEALTH_MULTIPLIER
                * multiplier;
        if (shroomie.isGolden()) {
            scorecard.add(shroomie.getValue() * GOLD_SCORE_MULTIPLIER * multiplier);
        } else {
            scorecard.add(shroomie.getValue() * SCORE_MULTIPLIER * multiplier);
        }
        multiplier = (shroomie.next == null) ? 1 : multiplier + 1;
    }

    private void decrement(int delta) {
        totalTimeInSun += delta;
        float damage = BASE_DAMAGE;
        timeInSun += delta;
        if (timeInSun > 3000) {
            damage *= 7;
        } else if (timeInSun > 1400) {
            damage *= 2;
        }
        damage = damage*target.getLuminosity()/8;
        bonusMeter -= damage;
        if (bonusMeter < 1)
            value -= damage;
        clamp();
    }

    public float totalAmountLost() {
        return totalDecrement;
    }

    public int totalTimeInSun() {
        return ((int) totalTimeInSun / 1000);
    }

    /**
     * test method TODO: DELETE
     */
//    public String playerMetrics() {
//        float totalTime = totalTimeInSun + totalTimeInShade;
//        float totalruntime = totalTimeRunning + totalTimeStanding;
//        return "Time in Sun: " + totalTimeInSun / totalTime
//                + "\n Time in Shadow: " + totalTimeInShade / totalTime + "\n"
//                + "Total time running " + totalTimeRunning / totalruntime
//                + "\n Total time standing: " + totalTimeStanding / totalruntime;
//    }

    public void reset() {
        value = BAR_MAX / 2;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        totalDecrement = 0;
        totalTimeInSun = 0;
        multiplier = 1;
        // TODO: why is this so much casting?
        // TODO: figure out how we SHOULD handle this issue. Mock players are
        // the devil.
        // try {
        // Player p = (Player) target;
        // p.setSmokeCount(0);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }
}
