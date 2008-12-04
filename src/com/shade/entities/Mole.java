package com.shade.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.State;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashGeom;
import com.shade.crash.CrashLevel;
import com.shade.entities.util.MoleFactory;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Geom;

public class Mole extends Linkable {

    private static final int MOLE_HEIGHT = 2;
    private static final float SPEED = .9f;
    private static final float SHADOW_THRESHOLD = .6f;

    private enum MoleState {
        SPAWNING, IDLE, SEEKING, WORKING, DEAD
    };

    private CrashLevel level;
    private StateManager manager;
    private MoleFactory factory;
    private Animation idling, working;
    private float luminosity;
    private float heading;
    private Mushroom target;

    public Mole(float x, float y, MoleFactory factory) throws SlickException {
        this.factory = factory;
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 12f);
    }

    private void initResources() throws SlickException {
        SpriteSheet idles = new SpriteSheet("entities/mole/sniff.png", 40, 40);
        idling = new Animation(idles, 300);
        idling.setAutoUpdate(false);
        idling.setPingPong(true);

        SpriteSheet works = new SpriteSheet("entities/mole/move.png", 40, 40);
        working = new Animation(works, 300);
        working.setAutoUpdate(false);
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new SpawningState());
        manager.add(new IdleState());
        manager.add(new SeekingState());
        manager.add(new WorkingState());
        manager.add(new DeadState());
    }

    private class SpawningState implements State {

        public void enter() {
            assert (next == null);
            assert (target == null);
        }

        public boolean isNamed(Object o) {
            return o == MoleState.SPAWNING;
        }

        public void onCollision(Entity obstacle) {
            assert (next == null);
            assert (target == null);
            manager.enter(MoleState.DEAD);
        }

        public void render(StateBasedGame game, Graphics g) {
            // don't render while spawning
        }

        public void update(StateBasedGame game, int delta) {
            // sunny so don't successfully spawn
            if (luminosity >= SHADOW_THRESHOLD) {
                manager.enter(MoleState.DEAD);
            }
            // shady spawn away
            if (getLuminosity() < SHADOW_THRESHOLD) {
                manager.enter(MoleState.IDLE);
            }
        }

    }

    private class IdleState implements State {

        private int timer;

        public void enter() {
            assert (next == null);
            assert (target == null);
            timer = 0;
            idling.restart();
            heading = (float) Math.PI;
        }

        public boolean isNamed(Object o) {
            return o == MoleState.IDLE;
        }

        public void onCollision(Entity obstacle) {
            // TODO what should happen in this case?
            assert (next == null);
            assert (target != null);
            if (obstacle.getRole() == Roles.MUSHROOM.ordinal()) {
                target = (Mushroom) obstacle;
                manager.enter(MoleState.WORKING);
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            idling.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;
            idling.update(delta);
            if (timer > 8000) {
                manager.enter(MoleState.DEAD);
            }

            if (foundTarget()) {
                manager.enter(MoleState.SEEKING);
            }
        }

    }

    private class SeekingState implements State {

        public void enter() {
            assert (next == null);
            assert (target != null);
        }

        public boolean isNamed(Object o) {
            return o == MoleState.SEEKING;
        }

        public void onCollision(Entity obstacle) {
            assert (next == null);
            assert (target != null);
            if (obstacle.getRole() == Roles.MUSHROOM.ordinal()) {
                heading += Math.PI;
                target = (Mushroom) obstacle;
                manager.enter(MoleState.WORKING);
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            working.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            assert (next == null);
            assert (target != null);

            working.update(delta);
            seekTarget();

            if (target.isDead()) {
                target = null;
                manager.enter(MoleState.IDLE);
            }
        }

    }

    private class WorkingState implements State {

        private int timer;

        public void enter() {
            assert (target != null);
            assert (next != null);
            timer = 0;
            working.restart();
        }

        public boolean isNamed(Object o) {
            return o == MoleState.WORKING;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
                heading += Math.PI / 2;
            }

            if (obstacle.getRole() == Roles.BASKET.ordinal() && next != null) {
                next.prev = (Linkable) obstacle;
                next = null;
                target = null;
                manager.enter(MoleState.DEAD);
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            working.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;
            working.update(delta);

            Vector2f v = Geom.calculateVector(SPEED, heading);
            xVelocity = v.x;
            yVelocity = v.y;
            nudge(xVelocity, yVelocity);

            if (timer > 5000 || target.isDead()) {
                detachAll();
                manager.enter(MoleState.DEAD);
            }
        }

        private void detachAll() {
            if (next != null) {
                Linkable head = next;
                while (head != null) {
                    head.detach();
                    head = next;
                }
                next = null;
                target = null;
            }
        }

    }

    private class DeadState implements State {

        private int timer;

        public void enter() {
            assert (next == null);
            assert (target == null);
            timer = 0;
        }

        public boolean isNamed(Object o) {
            return o == MoleState.DEAD;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
                factory.remove(Mole.this);
                level.remove(Mole.this);
            }
        }

        public void render(StateBasedGame game, Graphics g) {

        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;

            if (timer > 2000) {
                factory.remove(Mole.this);
                level.remove(Mole.this);
            }
        }

    }
    
    private boolean isDead() {
        return manager.currentState().isNamed(MoleState.SPAWNING) ||
            manager.currentState().isNamed(MoleState.DEAD);
    }

    public int getRole() {
        if (isDead()) {
            return Roles.SPAWNLING.ordinal();
        }
        return Roles.MOLE.ordinal();
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public int getZIndex() {
        return MOLE_HEIGHT;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level<?> l) {
        level = (CrashLevel) l;
    }

    public void removeFromLevel(Level<?> l) {

    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getXCenter(), getYCenter(), (float) Math.toDegrees(heading));
        manager.render(game, g);
        g.resetTransform();
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
    }

    public int compareTo(LuminousEntity o) {
        return getZIndex() - o.getZIndex();
    }

    private boolean foundTarget() {
        LuminousEntity[] entities = level.nearbyEntities(this, 200);

        boolean lineOfSight = false;
        int i = 0;
        while (!lineOfSight && i < entities.length) {
            if (((Entity) entities[i]).getRole() == Roles.MUSHROOM.ordinal()) {
                lineOfSight = level.lineOfSight(this, entities[i], Mole.this);
            }
            i++;
        }
        i--;

        if (lineOfSight) {
            target = (Mushroom) entities[i];
            return true;
        }
        return false;
    }

    private void seekTarget() {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(target, this);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (getX() < target.getX()) {
            d[1] = CrashGeom
                    .distance2(target, getXCenter() + 800, getYCenter());
        } else {
            d[1] = CrashGeom.distance2(this, target.getXCenter() + 800, target
                    .getYCenter());
        }

        // if I'm above my target
        if (getY() < target.getY()) {
            d[2] = CrashGeom
                    .distance2(target, getXCenter(), getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(this, target.getXCenter(), target
                    .getYCenter() + 600);
        }

        heading = CrashGeom.calculateAngle(target, this);
        if (d[1] < d[0] || d[2] < d[0]) {
            heading += Math.PI;
        }

        Vector2f v = Geom.calculateVector(SPEED, heading);
        xVelocity = v.x;
        yVelocity = v.y;
        nudge(xVelocity, yVelocity);
    }

}
