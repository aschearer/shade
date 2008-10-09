package com.shade.controls;

import java.util.LinkedList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.*;
import org.newdawn.slick.geom.Vector2f;

import com.shade.entities.*;
import com.shade.entities.util.*;
import com.shade.shadows.*;
import com.shade.shadows.ShadowEntity.ShadowIntensity;

/**
 * Manage the in-game state's triggers so the game can easily scale in
 * difficulty.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class InGameControl {

    private ShadowLevel level;
    private Player player;
    private CounterControl counter;
    private MeterControl meter;
    private int timer;
    private int moles;
    private LinkedList<Trigger> triggers;

    public InGameControl(ShadowLevel l, CounterControl c, MeterControl m, Player p) {
        level = l;
        counter = c;
        meter = m;
        player = p;
        timer = 0;
        moles = 0;
        triggers = new LinkedList<Trigger>();
        initTriggers();
    }

    private void initTriggers() {
        triggers.add(new TimedMushroom());
        triggers.add(new RandomMushroom());
        triggers.add(new MoleMaker());
        triggers.add(new DaylightImpact());
    }

    public void update(StateBasedGame game, int delta) throws SlickException {
        timer += delta;
        for (Trigger t : triggers) {
            if (t.isActive()) {
                t.update(game, delta);
            }
        }
    }

    private class DaylightImpact implements Trigger {
        
        private static final float INCREMENT = .01f;
        private float value = .06f;

        public boolean isActive() {
            return true;
        }

        public void update(StateBasedGame game, int delta)
                throws SlickException {
            if (player.hasIntensity(ShadowIntensity.UNSHADOWED)) {
                meter.decrement(value);
            }
            if (value < .07f && counter.value > 15) {
                value += INCREMENT;
            }
            if (value < .08f && counter.value > 25) {
                value += INCREMENT;
            }
            if (value < .09f && counter.value > 75) {
                value += INCREMENT;
            }
            if (value < .1f && counter.value > 150) {
                value += INCREMENT; // whoa...
            }
        }

    }

    /* TimedMushroom {{{ */
    private class TimedMushroom implements Trigger {

        private int timer;

        public boolean isActive() {
            return true;
        }

        public void update(StateBasedGame game, int delta)
                throws SlickException {
            timer += delta;
            if (timer > 6000) {
                timer = 0;
                Vector2f p = level.randomPoint(game.getContainer());
                level.add(MushroomFactory.makeMushroom(p.x, p.y));
            }
        }

    }

    /* }}} */

    /* RandomMushroom {{{ */
    private class RandomMushroom implements Trigger {

        private int timer;
        private double threshold = .9965;

        public boolean isActive() {
            return true;
        }

        public void update(StateBasedGame game, int delta)
                throws SlickException {
            timer += delta;
            if (timer > 5000 && Math.random() > threshold) {
                timer = 0;
                Vector2f p = level.randomPoint(game.getContainer());
                level.add(MushroomFactory.makeMushroom(p.x, p.y));
            }
            if (threshold > .9964 && counter.value > 25) {
                threshold = .9964;
            }
            if (threshold > .9963 && counter.value > 50) {
                threshold = .9963;
            }
            if (threshold > .9962 && counter.value > 100) {
                threshold = .9962;
            }
        }
    }

    /* }}} */

    /* MoleMaker {{{ */
    private class MoleMaker implements Trigger {

        public boolean isActive() {
            return moles < 5;
        }

        public void update(StateBasedGame game, int delta)
                throws SlickException {
            if (counter.value >= 5 && moles < 1) {
                level.add(new Mole());
                moles++;
            }
            if (counter.value > 25 && moles < 2) {
                level.add(new Mole());
                moles++;
            }
            if (counter.value > 50 && moles < 3) {
                level.add(new Mole());
                moles++;
            }
            if (counter.value > 100 && moles < 4) {
                level.add(new Mole());
                moles++;
            }
            if (counter.value > 200 && moles < 5) {
                level.add(new Mole());
                moles++;
            }
        }
    }
    /* }}} */
}
