package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.util.CrashGeom;
import com.shade.util.Geom;

public class Mushroom extends Body {

    private enum Status {
        IDLE, PICKED
    };

    private static final float H_RADIUS = 3f;
    private static final float SCALE_INCREMENT = .1f;
    private static final float MAX_SCALE = 5f;
    private static final int MAX_DISTANCE = 2500;
    private static final float SPEED = 1.5f;

    private Status currentStatus;
    private float timer;
    private float scale;

    /**
     * Mushrooms are a linked list!
     * 
     * Things worth noting... 1. When not picked both prev and next should be
     * null. 2. When picked the head of the list will be the player! 3. Every
     * other item on the list will be a shroom.
     */
    public Body prev, next;

    public Mushroom(float x, float y) {
        initShape(x, y);
        currentStatus = Status.IDLE;
        scale = 1;
    }

    private void initShape(float x, float y) {
        shape = new Circle(x - H_RADIUS, y - H_RADIUS, H_RADIUS);
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.PLAYER) {
            currentStatus = Status.PICKED;
        }
    }

    public void render(Graphics g) {
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (currentStatus == Status.IDLE && timer > 300) {
            timer = 0;
            if (scale < MAX_SCALE) {
                scale += SCALE_INCREMENT;
                grow();
            }
            /* Turn to a monster */
        }
        if (currentStatus == Status.PICKED
                && CrashGeom.distance2(prev, this) > MAX_DISTANCE) {
            float angle = CrashGeom.calculateAngle(prev, this);
            move(SPEED, angle);
        }
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        Transform t = Transform.createTranslateTransform(d.x, d.y);
        shape = shape.transform(t);
    }

    private void grow() {
        ((Circle) shape).setRadius(H_RADIUS * scale);
    }

}
