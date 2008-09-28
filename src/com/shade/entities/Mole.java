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
import com.shade.util.LevelUtil;

public class Mole extends Linkable implements ShadowCaster {
    
    private static final float SPEED = .7f;
    
    private enum Status {
        DIGGING, WAKING, IDLING, SEEKING, WORKING
    }
    
    private ShadowLevel level;
    private Status status;
    private Mushroom target;
    private float heading;
    private int timer, cooldown;

    public Mole(int cool) {
        initShape();
        status = Status.DIGGING;
        cooldown = cool;
    }

    private void initShape() {
        shape = new Circle(0, 0, 6);
    }

    public Shape castShadow(float direction) {
        return null;
    }

    public int getZIndex() {
        return 3;
    }

    public int compareTo(ShadowCaster s) {
        return getZIndex() - s.getZIndex();
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.MOLE;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.MUSHROOM) {
            // start working
            heading += Math.PI;
            target = (Mushroom) obstacle;
            status = Status.WORKING;
            timer = 0;
        }
        
        if (status == Status.WORKING && obstacle.getRole() == Role.OBSTACLE) {
            // change direction
            heading = (float) (Math.random() * 2 * Math.PI);
        }
        
        if (status == Status.SEEKING && obstacle.getRole() == Role.OBSTACLE) {
            // die
            stopWork();
        }
        
        if (status == Status.WAKING && obstacle.getRole() == Role.OBSTACLE) {
            // back underground
            status = Status.DIGGING;
        }
        if (obstacle.getRole() == Role.PLAYER) {
            // he got ya
            stopWork();
        }
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void render(StateBasedGame game, Graphics g) {
        if (status == Status.DIGGING) {
            return;
        }
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (status == Status.DIGGING && timer > cooldown) {
            Vector2f p = LevelUtil.randomPoint(game.getContainer());
            shape.setCenterX(p.x);
            shape.setCenterY(p.y);
            status = Status.WAKING;
            timer = 0;
        }
        if (status == Status.WAKING && timer > cooldown) {
            // wake up!
            timer = 0;
            status = Status.IDLING;
        }
        if (status == Status.IDLING && findTarget()) {
            // target found
            status = Status.SEEKING;
        }
        if (status == Status.IDLING && timer > cooldown) {
            // go back underground start over
            stopWork();
        }
        if (status == Status.SEEKING) {
            // move towards target
            heading = CrashGeom.calculateAngle(target, this);
            move(SPEED, heading);
        }
        if (status == Status.SEEKING && target.isDead()) {
            stopWork();
        }
        if (status == Status.WORKING) {
            // move the target
            move(SPEED, heading);
        }
        if (status == Status.WORKING && timer > cooldown) {
            stopWork();
        }
        if (status == Status.WORKING && target.isDead()) {
            stopWork();
        }
        testAndWrap();
    }

    private boolean findTarget() {
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
            status = Status.SEEKING;
            return true;
        }
        return false;
    }
    
    private void stopWork() {
        status = Status.DIGGING;
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
    
    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
    }

    public void repel(Entity repellee) {
        // TODO Auto-generated method stub
        
    }

}
