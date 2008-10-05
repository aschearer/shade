package com.shade.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
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
import com.shade.shadows.ShadowLevel.DayLightStatus;
import com.shade.util.Geom;

public class Monster extends Linkable implements ShadowEntity {

    private static final float SPEED = .9f;
    private static final float SIZE = 20;

    public enum MonsterState {
        SLEEPING, WANDERING, ATTACKING, COOLING
    };

    private float heading;
    private ShadowLevel level;
    private Player target;
    private ShadowIntensity shadowStatus;

    private Animation sleeping, wandering, attacking, cooling;
    private StateManager manager;

    public Monster(float x, float y) throws SlickException {
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, SIZE);
    }

    private void initResources() throws SlickException {
        SpriteSheet sleeps = new SpriteSheet("entities/mole/sniff.png", 40, 40);

        sleeping = new Animation(sleeps, 300);
        sleeping.setAutoUpdate(false);
        sleeping.setPingPong(true);

        SpriteSheet moves = new SpriteSheet("entities/mole/move.png", 40, 40);

        wandering = new Animation(moves, 300);
        wandering.setAutoUpdate(false);

        cooling = new Animation(sleeps, 300);
        cooling.setAutoUpdate(false);
        cooling.setPingPong(true);

        attacking = new Animation(moves, 300);
        attacking.setAutoUpdate(false);
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new SleepingState());
        manager.add(new WanderingState());
        manager.add(new AttackingState());
        manager.add(new CoolingState());
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
        xVelocity = d.x;
        yVelocity = d.y;
    }

    private class SleepingState implements State {

        public boolean equals(Object state) {
            return state == MonsterState.SLEEPING;
        }

        public void enter() {
            sleeping.restart();
            heading = (float) Math.PI;
        }

        public void update(StateBasedGame game, int delta) {
            sleeping.update(delta);
            if (level.getDayLight() == DayLightStatus.NIGHT) {
                manager.enter(MonsterState.WANDERING);
            }
        }

        public void render(Graphics g) {
            sleeping.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {

        }
    }

    private class WanderingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MonsterState.WANDERING;
        }

        public void enter() {
            wandering.restart();
            timer = 0;
        }

        public void update(StateBasedGame game, int delta) {
            wandering.update(delta);
            if (level.getDayLight() != DayLightStatus.NIGHT) {
                manager.enter(MonsterState.SLEEPING);
                return;
            }

            if (findTarget()) {
                manager.enter(MonsterState.ATTACKING);
                return;
            }

            updateHeading(delta);
            move(SPEED, heading);
        }

        public void render(Graphics g) {
            wandering.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {

        }

        private void updateHeading(int delta) {
            timer += delta;
            if (timer > 3000) {
                heading += (float) (Math.random() * Math.PI / 2);
                heading *= (Math.random() >= .5) ? -1 : 1;
                timer = 0;
            }
        }

        /**
         * Return true if the monster can "see" the target. Called whenever the
         * monster is wandering or attacking. The monster will only seek the
         * target if he has found it. Otherwise the monster will simply wander
         * aimlessly.
         * 
         * @return
         */
        private boolean findTarget() {
            ShadowEntity[] entities = level.nearByEntities(Monster.this, 300);

            boolean lineOfSight = false;
            int i = 0;
            while (!lineOfSight && i < entities.length) {
                if (((Entity) entities[i]).getRole() == Role.PLAYER) {
                    lineOfSight = level.lineOfSight(Monster.this, entities[i]);
                }
                i++;
            }
            i--;

            if (lineOfSight) {
                target = (Player) entities[i];
                return true;
            }
            return false;
        }
    }

    private class AttackingState implements State {

        public boolean equals(Object state) {
            return state == MonsterState.ATTACKING;
        }

        public void enter() {
            attacking.restart();
        }

        public void update(StateBasedGame game, int delta) {
            attacking.update(delta);
            if (level.getDayLight() != DayLightStatus.NIGHT) {
                manager.enter(MonsterState.SLEEPING);
                return;
            }

            if (!findTarget()) {
                manager.enter(MonsterState.WANDERING);
                return;
            }

            seekTarget();
        }

        public void render(Graphics g) {
            attacking.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.PLAYER) {
                Player p = (Player) obstacle;
                heading = CrashGeom.calculateAngle(p, Monster.this);
                manager.enter(MonsterState.COOLING);
                return;
            }

        }

        /**
         * Return true if the monster can "see" the target. Called whenever the
         * monster is wandering or attacking. The monster will only seek the
         * target if he has found it. Otherwise the monster will simply wander
         * aimlessly.
         * 
         * @return
         */
        private boolean findTarget() {
            ShadowEntity[] entities = level.nearByEntities(Monster.this, 300);

            boolean lineOfSight = false;
            int i = 0;
            while (!lineOfSight && i < entities.length) {
                if (((Entity) entities[i]).getRole() == Role.PLAYER) {
                    lineOfSight = level.lineOfSight(Monster.this, entities[i]);
                }
                i++;
            }
            i--;

            if (lineOfSight) {
                target = (Player) entities[i];
                return true;
            }
            return false;
        }

        /**
         * Move one step closer to your target.
         */
        private void seekTarget() {
            float[] d = new float[3];
            d[0] = CrashGeom.distance2(target, Monster.this);
            if (d[0] > 500 && target.hasIntensity(ShadowIntensity.CASTSHADOWED)) {
                return;
            }
            d[1] = d[0];
            d[2] = d[0];
            // if I'm left of my target
            if (getX() < target.getX()) {
                d[1] = CrashGeom.distance2(target, getCenterX() + 800,
                                           getCenterY());
            } else {
                d[1] = CrashGeom.distance2(Monster.this,
                                           target.getCenterX() + 800, target
                                                   .getCenterY());
            }

            // if I'm above my target
            if (getY() < target.getY()) {
                d[2] = CrashGeom.distance2(target, getCenterX(),
                                           getCenterY() + 600);
            } else {
                d[2] = CrashGeom.distance2(Monster.this, target.getCenterX(),
                                           target.getCenterY() + 600);
            }

            heading = CrashGeom.calculateAngle(target, Monster.this);
            if (d[1] < d[0] || d[2] < d[0]) {
                heading += Math.PI;
            }

            move(SPEED, heading);
        }
    }

    private class CoolingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == MonsterState.COOLING;
        }

        public void enter() {
            cooling.restart();
            timer = 0;
            heading = (float) Math.PI;
        }

        public void update(StateBasedGame game, int delta) {
            cooling.update(delta);
            if (level.getDayLight() != DayLightStatus.NIGHT) {
                manager.enter(MonsterState.SLEEPING);
                return;
            }

            timer += delta;
            if (timer > 5000) {
                manager.enter(MonsterState.WANDERING);
                return;
            }
        }

        public void render(Graphics g) {
            cooling.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {

        }
    }

    public Shape castShadow(float direction, float depth) {
        float r = ((Circle) shape).radius;
        float h = getZIndex() * depth * 1.6f;
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }

    public int getZIndex() {
        return 5;
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.MONSTER;
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
        if (obstacle.getRole() == Role.OBSTACLE) {
            obstacle.repel(this);
        }
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

    public boolean isActive() {
        return manager.currentState().equals(MonsterState.ATTACKING)
                || manager.currentState().equals(MonsterState.WANDERING);
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

    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public int compareTo(ShadowEntity s) {
        return getZIndex() - s.getZIndex();
    }
}
