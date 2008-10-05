package com.shade.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.util.CrashGeom;
import com.shade.entities.util.State;
import com.shade.entities.util.StateManager;
import com.shade.shadows.ShadowEntity;
import com.shade.shadows.ShadowLevel;
import com.shade.util.Geom;

public class Mole extends Linkable implements ShadowEntity {

    private static final int RADIUS = 12;
    private static final float SPEED = .7f;

    private enum MoleState {
        DIGGING, WAKING, IDLING, SEEKING, WORKING, CONFUSED
    }

    public ShadowLevel level;
    private StateManager manager;
    private Mushroom target;
    private float heading;
    private Animation waking, idling, seeking, working;
    private ShadowIntensity shadowStatus;

    public Mole() throws SlickException {
        initShape();
        initResources();
        initStates();
    }

    private void initShape() {
        shape = new Circle(0, 0, RADIUS);
    }

    private void initResources() throws SlickException {
        SpriteSheet wakes = new SpriteSheet("entities/mole/sniff.png", 40, 40);

        waking = new Animation(wakes, 300);
        waking.setAutoUpdate(false);
        waking.setPingPong(true);

        idling = waking;

        SpriteSheet seeks = new SpriteSheet("entities/mole/move.png", 40, 40);

        seeking = new Animation(seeks, 300);
        seeking.setAutoUpdate(false);

        working = seeking;
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new DiggingState());
        manager.add(new WakingState());
        manager.add(new IdlingState());
        manager.add(new SeekingState());
        manager.add(new WorkingState());
    }

    private class DiggingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MoleState.DIGGING;
        }

        public void enter() {
            timer = 0;
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

        public void onCollision(Entity obstacle) {
            // TODO Auto-generated method stub

        }

        public void render(Graphics g) {
            // TODO Auto-generated method stub

        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;
            if (timer > 4000) {
                Vector2f p = level.randomPoint(game.getContainer());
                shape.setCenterX(p.x);
                shape.setCenterY(p.y);
                manager.enter(MoleState.WAKING);
            }
        }

    }

    private class WakingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MoleState.WAKING;
        }

        public void enter() {
            waking.restart();
            timer = 0;
            heading = (float) Math.PI;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.MUSHROOM) {
                heading += Math.PI;
                target = (Mushroom) obstacle;
                manager.enter(MoleState.WORKING);
            }
        }

        public void render(Graphics g) {
            waking.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            waking.update(delta);
            timer += delta;
            if (timer > 3000) {
                manager.enter(MoleState.IDLING);
            }
        }

    }

    private class IdlingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MoleState.IDLING;
        }

        public void enter() {
            idling.restart();
            timer = 0;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.MUSHROOM) {
                heading += Math.PI;
                target = (Mushroom) obstacle;
                manager.enter(MoleState.WORKING);
            }
        }

        public void render(Graphics g) {
            idling.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            idling.update(delta);
            timer += delta;
            if (timer > 2000) {
                manager.enter(MoleState.DIGGING);
            }

            if (findTarget()) {
                manager.enter(MoleState.SEEKING);
                return;
            }
        }

    }

    private class SeekingState implements State {

        public boolean equals(Object state) {
            return state == MoleState.SEEKING;
        }

        public void enter() {
            seeking.restart();
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.OBSTACLE) {
                manager.enter(MoleState.DIGGING);
            }

            if (obstacle.getRole() == Role.MUSHROOM) {
                heading += Math.PI;
                target = (Mushroom) obstacle;
                manager.enter(MoleState.WORKING);
            }
        }

        public void render(Graphics g) {
            seeking.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            seeking.update(delta);
            seekTarget();

            if (target.isDead()) {
                manager.enter(MoleState.IDLING);
            }
        }

    }

    private class WorkingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MoleState.WORKING;
        }

        public void enter() {
            working.restart();
            timer = 0;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.OBSTACLE) {
                heading += Math.PI / 2;
            }
        }

        public void render(Graphics g) {
            working.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            working.update(delta);
            timer += delta;
            move(SPEED, heading);

            if (timer > 5000 || target.isDead()) {
                manager.enter(MoleState.DIGGING);
            }
        }

    }

    private boolean findTarget() {
        ShadowEntity[] entities = level.nearByEntities(this, 300);

        boolean lineOfSight = false;
        int i = 0;
        while (!lineOfSight && i < entities.length) {
            if (((Entity) entities[i]).getRole() == Role.MUSHROOM) {
                lineOfSight = level.lineOfSight(this, entities[i]);
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
                    .distance2(target, getCenterX() + 800, getCenterY());
        } else {
            d[1] = CrashGeom.distance2(this, target.getCenterX() + 800, target
                    .getCenterY());
        }

        // if I'm above my target
        if (getY() < target.getY()) {
            d[2] = CrashGeom
                    .distance2(target, getCenterX(), getCenterY() + 600);
        } else {
            d[2] = CrashGeom.distance2(this, target.getCenterX(), target
                    .getCenterY() + 600);
        }

        heading = CrashGeom.calculateAngle(target, this);
        if (d[1] < d[0] || d[2] < d[0]) {
            heading += Math.PI;
        }

        move(SPEED, heading);
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
    }

    public int getZIndex() {
        return 4;
    }

    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.MOLE;
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void repel(Entity repellee) {
        Body b = (Body) repellee;
        double playerx = b.getCenterX();
        double playery = b.getCenterY();
        double dist_x = playerx - getCenterX();
        double dist_y = playery - getCenterY();
        double mag = Math.sqrt(dist_x * dist_x + dist_y * dist_y);
        double playradius = b.getWidth() / 2;
        double obstacleradius = getWidth() / 2;
        double angle = Math.atan2(dist_y, dist_x);
        double move = (playradius + obstacleradius - mag) * 1.5;
        b.move(Math.cos(angle) * move, Math.sin(angle) * move);
    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getCenterX(), getCenterY(), (float) Math.toDegrees(heading));
        manager.render(g);
        g.resetTransform();
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();
    }

    public int compareTo(ShadowEntity o) {
        return getZIndex() - o.getZIndex();
    }

}
