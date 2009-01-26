package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.Player;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;
import com.sun.tools.jdi.TargetVM;

/**
 * A meter which can go from zero to a hundred. + The control should be notified
 * whenever a mushroom is collected. + The control should + When the control
 * reaches zero it will fire an event.
 * 
 * @author aas11
 * 
 */
public class MeterControl implements ControlSlice, MushroomCounter {

    public static final float BASE_DAMAGE = 0.05f;
    public static final float BASE_EXPONENT = 1.0005f;
    public static final float GOLD_SCORE_MULTIPLIER = 40;
    public static final float HEALTH_MULTIPLIER = 1f;
    public static final float BAR_MAX = 40f;
    public static final float BONUS_SCALE = 1.5f;

    private Player target;
    private ControlListener listener;
    private ScoreControl scorecard;

    private float x, y;
    private float value, totalAmountToAdd, rateOfChange;
    private float totalDecrement;
    private int totalTimeInSun;
    private static Image front, back, danger, current;
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
        value = 0;
        totalAmountToAdd = 0;
        rateOfChange = 1;
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
        if (value == 0) {
            // listener.fire(this);
            target.stun();
            totalAmountToAdd = BAR_MAX / 3;
            rateOfChange = 3;
            timeInSun = 0;
        }
        
        if (totalAmountToAdd > 0) {
            value += .1f * rateOfChange;
            totalAmountToAdd -= .1f * rateOfChange;
        } else {
            rateOfChange = 1;
        }
        
        if (target.isStunned()) {
            return;
        }
        

        if (target != null
                && target.getLuminosity() > MasterState.SHADOW_THRESHOLD) {
            decrement(delta);
        } else {
            timeInSun = 0;
        }
        clamp();
        
        target.setSpeed(Player.MIN_SPEED + (value / BAR_MAX) * (Player.MAX_SPEED - Player.MIN_SPEED));
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
        if (value > BAR_MAX + 5) {
            scorecard.add(1);
            value--;
        }
    }

    private void valueMushroom(Mushroom shroomie) {
        totalAmountToAdd += shroomie.getValue() * HEALTH_MULTIPLIER;
        if (shroomie.isGolden()) {
            scorecard.add(shroomie.getValue() * GOLD_SCORE_MULTIPLIER);
        }
    }

    private void decrement(int delta) {
        float damage = BASE_DAMAGE;
        timeInSun += delta;
        
        if (timeInSun > 1500) {
            damage *= 7;
        } else if (timeInSun > 1000) {
            damage *= 2;
        }
        value -= damage;
        clamp();
    }

    public float totalAmountLost() {
        return totalDecrement;
    }

    public int totalTimeInSun() {
        return (totalTimeInSun / 1000);
    }

    public void reset() {
        value = BAR_MAX / 2;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        totalDecrement = 0;
        totalTimeInSun = 0;
        // TODO: why is this so much casting?
        // TODO: figure out how we SHOULD handle this issue. Mock players are
        // the devil.
        try {
            Player p = (Player) target;
            p.setSmokeCount(0);
        } catch (Exception e) {

        }
    }
}
