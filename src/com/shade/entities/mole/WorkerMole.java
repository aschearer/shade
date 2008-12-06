package com.shade.entities.mole;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.util.State;
import com.shade.crash.CrashGeom;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.util.Geom;

/**
 * A mole who has zero or more mushrooms in toe but hasn't yet returned
 * underground.
 * 
 * Working moles: + Have identified a target mushroom + Have zero or more
 * mushrooms attached + Will try to grab the nearest uncollected mushroom +
 * Return underground after a set amount of time
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class WorkerMole implements State {

    private static final int WORK_TIME = 5000;
    private Mole mole;
    private Animation working;
    private int timer;

    public WorkerMole(Mole mole) throws SlickException {
        this.mole = mole;
        initResources();
    }

    private void initResources() throws SlickException {
        SpriteSheet works = new SpriteSheet("entities/mole/move.png", 40, 40);
        working = new Animation(works, 300);
        working.setAutoUpdate(false);
    }

    public void enter() {
        working.restart();
        timer = 0;
    }

    public int getRole() {
        return Roles.MOLE.ordinal();
    }

    public boolean isNamed(Object o) {
        return o == Mole.States.WORKING;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.equals(mole.target)) {
            mole.target = null;
            timer = 0;
        }

        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            mole.kill();
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        working.draw(mole.getX(), mole.getY(), mole.getWidth(), mole
                .getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        working.update(delta);
        testTimer(delta);
        testForTarget(mole);
        testAndMove(mole);
    }

    private void testTimer(int delta) {
        timer += delta;
        if (timer > WORK_TIME) {
            mole.kill();
            // TODO dig a hole and escape w/ your catch
        }
    }

    private void testForTarget(Mole mole) {
        if (eligibleForWork(mole)) {
            mole.target = null;
            Util.foundTarget(mole);
        }
    }

    private boolean eligibleForWork(Mole mole2) {
        return (mole.target == null || !aMushroom(mole.target))
                && mole.mushroomsCollected() < 3;
    }

    private boolean aMushroom(Mushroom target) {
        return (mole.target.getRole() == Roles.MUSHROOM.ordinal());
    }

    private void testAndMove(Mole mole) {
        if (mole.target != null) {
            seekTarget();
        } else {
            mole.nudge(mole.getXVelocity(), mole.getYVelocity());
        }
    }

    private void seekTarget() {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(mole.target, mole);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (mole.getX() < mole.target.getX()) {
            d[1] = CrashGeom.distance2(mole.target, mole.getXCenter() + 800,
                    mole.getYCenter());
        } else {
            d[1] = CrashGeom.distance2(mole, mole.target.getXCenter() + 800,
                    mole.target.getYCenter());
        }

        // if I'm above my target
        if (mole.getY() < mole.target.getY()) {
            d[2] = CrashGeom.distance2(mole.target, mole.getXCenter(), mole
                    .getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(mole, mole.target.getXCenter(),
                    mole.target.getYCenter() + 600);
        }

        mole.heading = CrashGeom.calculateAngle(mole.target, mole);
        if (d[1] < d[0] || d[2] < d[0]) {
            mole.heading += Math.PI;
        }

        Vector2f v = Geom.calculateVector(.9f, mole.heading);
        mole.nudge(v.x, v.y);
    }

}
