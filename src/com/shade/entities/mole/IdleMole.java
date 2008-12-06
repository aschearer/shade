package com.shade.entities.mole;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.entities.Roles;

/**
 * A mole who has not yet found any mushrooms but is searching for them.
 *
 * Idle moles: + Have not identified a target mushroom + Have zero mushrooms
 * attached + Are checking for line-of-sight with nearby mushrooms + Return
 * underground after a set amount of time
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class IdleMole implements State {

    private Mole mole;
    private Animation idling;
    private int timer;

    public IdleMole(Mole mole) throws SlickException {
        this.mole = mole;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/mole/sniff.png", 40, 40);
        idling = new Animation(idles, 300);
        idling.setAutoUpdate(false);
        idling.setPingPong(true);
    }

    public void enter() {
        timer = 0;
        idling.restart();
    }

    public int getRole() {
        return Roles.MOLE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mole.States.IDLE;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.MUSHROOM.ordinal()) {
            mole.manager.enter(Mole.States.WORKING);
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        idling.draw(mole.getX(), mole.getY(), mole.getWidth(), mole.getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        idling.update(delta);
        testTimer(delta);
        testForTarget(mole);
    }

    private void testTimer(int delta) {
        timer += delta;
        if (timer > 10000) {
            mole.kill();
        }
    }

    private void testForTarget(Mole mole) {
        if (timer > 5000 && Util.foundTarget(mole)) {
            mole.manager.enter(Mole.States.WORKING);
        }
    }

}
