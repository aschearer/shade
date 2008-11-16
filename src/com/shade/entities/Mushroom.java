package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
import com.shade.crash.Repelable;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Geom;

public class Mushroom extends Linkable {

    private static final float SHADOW_THRESHOLD = .5f;
    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .005f;
    private static final float MAX_SCALE = 3.5f;
    private static final float MIN_SCALE = 1.5f;
    private static final float START_SCALE = MIN_SCALE + .5f;
    private static final float SPEED = 1.6f;

    private enum MushroomState {
        SPAWNING, NORMAL, PICKED, COLLECTED, DEAD
    };

    public enum MushroomType {
        POISON, NORMAL, EGG, GOOD, RARE
    };

    public MushroomType type;
    private Level<LuminousEntity> level;
    private StateManager manager;
    private float luminosity;
    private Image mushroom;
    private float scale;
    private float myIntensity;

    public Mushroom(float x, float y, MushroomType t) throws SlickException {
        initShape(x, y);
        initResources(t);
        initStates();
        scale = START_SCALE;
        type = t;
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, RADIUS * START_SCALE);
    }

    private void initResources(MushroomType t) throws SlickException {
        SpriteSheet s = new SpriteSheet("entities/mushroom/mushrooms.png", 40,
                40);
        mushroom = s.getSprite(t.ordinal(), 0);
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new SpawningState());
        manager.add(new NormalState());
        manager.add(new PickedState());
        manager.add(new CollectedState());
        manager.add(new DeadState());
    }

    private class SpawningState implements State {

        public boolean isNamed(Object state) {
            return state == MushroomState.SPAWNING;
        }

        public void enter() {

        }

        public void onCollision(Entity obstacle) {
            assert (prev == null);
            enter(MushroomState.DEAD);
        }

        public void render(StateBasedGame game, Graphics g) {
            // don't render till normal
        }

        public void update(StateBasedGame game, int delta) {
            // sunny so don't successfully spawn
            if (luminosity >= SHADOW_THRESHOLD) {
                enter(MushroomState.DEAD);
            }
            // shady spawn away
            if (getLuminosity() < SHADOW_THRESHOLD && scale < MAX_SCALE) {
                enter(MushroomState.NORMAL);
            }
        }
    }

    private class NormalState implements State {

        public boolean isNamed(Object state) {
            return state == MushroomState.NORMAL;
        }

        public void enter() {

        }

        public void onCollision(Entity obstacle) {
            assert (prev == null);
            if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
                manager.enter(MushroomState.PICKED);
                ((Linkable) obstacle).attach(Mushroom.this);
                return;
            }
            // TODO implement mushroom collision
        }

        public void render(StateBasedGame game, Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }
            // TODO implement mushroom monster
            
            // sunny shrink
            if (luminosity >= SHADOW_THRESHOLD) {
                shrink();
                resize();
                return;
            }
            // shady grow
            if (getLuminosity() < SHADOW_THRESHOLD && scale < MAX_SCALE) {
                grow();
                resize();
                return;
            }
        }
    }

    private class PickedState implements State {

        public boolean isNamed(Object state) {
            return state == MushroomState.PICKED;
        }

        public void enter() {

        }

        public void onCollision(Entity obstacle) {
            // TODO implement mushroom collision
        }

        public void render(StateBasedGame game, Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (prev == null) {
                manager.enter(MushroomState.NORMAL);
                return;
            }

            if (prev.getRole() == Roles.BASKET.ordinal()) {
                manager.enter(MushroomState.COLLECTED);
                return;
            }

            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }
            // TODO implement mushroom monster

            // sunny shrink
            if (getLuminosity() >= SHADOW_THRESHOLD) {
                shrink();
                resize();
            }
            
            // way too far away, break off
            if (overThreshold(prev, 12000)) {
                detach();
                manager.enter(MushroomState.NORMAL);
                return;
            }

            // too far away, catch up
            if (overThreshold(prev, 1200)) {
                followLeader();
                testAndWrap();
                return;
            }
        }
    }

    private class CollectedState implements State {

        public boolean isNamed(Object state) {
            return state == MushroomState.COLLECTED;
        }

        public void enter() {

        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Roles.BASKET.ordinal()) {
                manager.enter(MushroomState.DEAD);
                return;
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (prev == null) {
                manager.enter(MushroomState.NORMAL);
                return;
            }

            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }

            // TODO implement mushroom monster

            // sunny shrink
            if (getLuminosity() >= SHADOW_THRESHOLD) {
                shrink();
                resize();
            }

            // way too far away, break off
            if (overThreshold(prev, 120000)) {
                detach();
                manager.enter(MushroomState.NORMAL);
                return;
            }

            followLeader();
            testAndWrap();
            return;
        }
    }

    private class DeadState implements State {

        public boolean isNamed(Object state) {
            return state == MushroomState.DEAD;
        }

        public void enter() {
            detach();
            level.remove(Mushroom.this);
        }

        public void onCollision(Entity obstacle) {
            // I'm dead stupid
        }

        public void render(StateBasedGame game, Graphics g) {
            // I'm dead stupid
        }

        public void update(StateBasedGame game, int delta) {
            // I'm dead stupid
        }

    }

    private void resize() {
        float x = shape.getCenterX();
        float y = shape.getCenterY();
        ((Circle) shape).setRadius(RADIUS * scale);
        shape.setCenterX(x);
        shape.setCenterY(y);
    }

    private void followLeader() {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(prev, Mushroom.this);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (getX() < prev.getX()) {
            d[1] = CrashGeom.distance2(prev, getXCenter() + 800, getYCenter());
        } else {
            d[1] = CrashGeom.distance2(Mushroom.this, prev.getXCenter() + 800,
                    prev.getYCenter());
        }

        // if I'm above my target
        if (getY() < prev.getY()) {
            d[2] = CrashGeom.distance2(prev, getXCenter(), getYCenter() + 600);
        } else {
            d[2] = CrashGeom.distance2(Mushroom.this, prev.getXCenter(), prev
                    .getYCenter() + 600);
        }

        float angle = CrashGeom.calculateAngle(prev, Mushroom.this);
        if (d[1] < d[0] || d[2] < d[0]) {
            angle += Math.PI;
        }

        move(SPEED, angle);
    }

    private void move(float magnitude, float angle) {
        Vector2f v = Geom.calculateVector(magnitude, angle);
        xVelocity = v.x;
        yVelocity = v.y;
        nudge(xVelocity, yVelocity);
    }

    public boolean isDead() {
        return manager.currentState().equals(MushroomState.DEAD);
    }

    public int getZIndex() {
        return 2;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(this);
        }
        manager.onCollision(obstacle);
    }

    public void render(StateBasedGame game, Graphics g) {
        manager.render(game, g);
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
    }

    public float getSize() {
        return scale;
    }

    private void grow() {
        scale += SCALE_INCREMENT;
    }

    private void shrink() {
        if (type == MushroomType.RARE) {
            scale -= SCALE_INCREMENT / 2;
            return;
        }
        scale -= SCALE_INCREMENT / 4;
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    /**
     * I don't want to hear about the casting issues. This will fail if it's not
     * the level isn't instantiated correctly.
     */
    @SuppressWarnings("unchecked")
    public void addToLevel(Level<?> l) {
        level = (Level<LuminousEntity>) l;
    }

    public int getRole() {
        return Roles.MUSHROOM.ordinal();
    }

    public void removeFromLevel(Level<?> l) {

    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }
}
