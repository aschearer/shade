package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.util.CrashGeom;
import com.shade.shadows.ShadowCaster;
import com.shade.shadows.ShadowLevel;
import com.shade.util.Geom;

public class Drone extends Linkable implements ShadowCaster {
    
    private static final float SPEED = 1f;

    private enum Status {
        IDLING, SEEKING, WORKING, INACTIVE
    };
    
    private Status currentStatus;
    private ShadowLevel level;
    private Mushroom target;
    private float heading;
    private int timer;

    public Drone(float x, float y, float r) {
        initShape(x, y, r);
        currentStatus = Status.IDLING;
    }

    private void initShape(float x, float y, float r) {
        shape = new Circle(x, y, r);
    }

    public Shape castShadow(float direction) {
        return null;
    }

    public int getZIndex() {
        return 2;
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.DRONE;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.MUSHROOM && currentStatus == Status.SEEKING) {
            timer = 0;
            heading = (float) (Math.random() * 2 * Math.PI);
            currentStatus = Status.WORKING;
        }
        if (obstacle.getRole() == Role.OBSTACLE) {
            stopWork();
        }
    }

    public void removeFromLevel(Level l) {

    }

    public void render(Graphics g) {
        if (currentStatus == Status.INACTIVE) {
            return;
        }
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        testAndMove(delta);
        testAndWrap();
    }

    private void testAndMove(int delta) {
        if (currentStatus == Status.IDLING) {
            findMushroom();
        }
        if (currentStatus == Status.SEEKING) {
            seek(delta);
        }
        if (currentStatus == Status.WORKING) {
            work(delta);
        }
        if (currentStatus == Status.INACTIVE) {
            timer += delta;
            if (timer > 4000) {
                
            }
        }
    }

    private void seek(int delta) {
        move(SPEED, heading);
    }

    private void work(int delta) {
        move(SPEED, heading);
        timer += delta;
        if (Math.random() > .999 || timer > 4000) {
            stopWork();
        }
    }

    private void stopWork() {
        target.detach();
        currentStatus = Status.INACTIVE;
        timer = 0;
    }
    
    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
    }

    private void findMushroom() {
        Mushroom[] shroomies = level.nearbyShrooms(this);
        
        boolean lineOfSight = false;
        int i = 0;
        while (!lineOfSight && i < shroomies.length) {
            lineOfSight = level.ray(this, shroomies[i]);
            i++;
        }
        i--;
        
        if (lineOfSight) {
            target = shroomies[i];
            currentStatus = Status.SEEKING;
            heading = CrashGeom.calculateAngle(target, this);
        }
    }

    public int compareTo(ShadowCaster s) {
        return getZIndex() - s.getZIndex();
    }

}
