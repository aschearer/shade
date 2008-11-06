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
import com.shade.util.Geom;

public class Bird extends Linkable implements ShadowEntity {

    private static final float SIZE = 20;

    public enum BirdState {
        ATTACKING, COOLING, WARNING
    };

    private float heading;
    private ShadowLevel level;
    private int BIRD_SPEED = 5;
    private ShadowIntensity shadowStatus;

    private Animation warning, cooling, attacking;
    private StateManager manager;

    public Bird(float x, float y) throws SlickException {
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, SIZE);
    }

    private void initResources() throws SlickException {

        SpriteSheet warns = new SpriteSheet("entities/bird/birdwarns.png", 40, 40);

        warning = new Animation(warns, 600);
        warning.setAutoUpdate(true);
        warning.setPingPong(true);
        
        SpriteSheet cools = new SpriteSheet("entities/bird/birdonhit.png", 40, 40);

        cooling = new Animation(cools, 600);
        cooling.setAutoUpdate(true);
        cooling.setPingPong(true);
        
        SpriteSheet attacks = new SpriteSheet("entities/bird/bird.png", 40, 40);

        attacking = new Animation(attacks, 300);
        attacking.setAutoUpdate(true);
        attacking.setPingPong(true);
        
        
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new WarningState());
        manager.add(new CoolingState());
        manager.add(new AttackingState());
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
        xVelocity = d.x;
        yVelocity = d.y;
    }

    private class WarningState implements State {

    	private int timer;
        public boolean equals(Object state) {
            return state == BirdState.WARNING;
        }

        public void enter() {
        	timer = 0;
            warning.restart();
        }

        public void update(StateBasedGame game, int delta) {
        	warning.update(delta);
            timer += delta;
            if (timer > 5000) {
                manager.enter(BirdState.ATTACKING);
                return;
            }
        }

        public void render(Graphics g) {
            warning.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {

        }


    }
    
    private class AttackingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == BirdState.ATTACKING;
        }

        public void enter() {
            attacking.restart();
            timer = 0;
            Entity e =level.getByRole(Role.PLAYER);
            heading =CrashGeom.calculateAngle((Body) e, Bird.this);
        }

        public void update(StateBasedGame game, int delta) {
            attacking.update(delta);
            timer += delta;
            move(BIRD_SPEED,heading);
        }

        public void render(Graphics g) {
            attacking.draw(getX(), getY());
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.PLAYER) {
                Player p = (Player) obstacle;
                heading = CrashGeom.calculateAngle(p, Bird.this);
                manager.enter(BirdState.COOLING);
            }
        }
    }

    private class CoolingState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == BirdState.COOLING;
        }

        public void enter() {
            cooling.restart();
            timer = 0;
        }

        public void update(StateBasedGame game, int delta) {
            cooling.update(delta);
            timer += delta;
            if (timer > 5000) {
                level.remove(Bird.this);
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
        return 55; // Birds should be above everything
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.BIRD;
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
    
    @Override protected void testAndWrap() {
        if (getCenterX() <= 5) {
            level.remove(this);
        }
        else if (getCenterX() > 795) {
        	level.remove(this);
        }
        else if (getCenterY() <= 5) {
        	level.remove(this);
        }
        else if (getCenterY() > 595) {
        	level.remove(this);
        }
    }

    public boolean isActive() {
        return (manager.currentState().equals(BirdState.ATTACKING)) ;
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

    public float getShadowIntensity() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void updateIntensity(Graphics g) {
        // TODO Auto-generated method stub
        
    }
}