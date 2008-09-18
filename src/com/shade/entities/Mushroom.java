package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.util.CrashGeom;
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

public class Mushroom extends Body implements ShadowCaster {

    private enum Status {
        IDLE, PICKED, DEAD
    };

    public boolean shaded;

    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .005f;
    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 1.2f;
    private static final int MAX_DISTANCE = 2500;
    private static final float SPEED = 1.5f;

    private Status currentStatus;
    private float timer;
    private float scale;

    private Image sprite;

    /**
     * Mushrooms are a linked list!
     * 
     * Things worth noting... 1. When not picked both prev and next should be
     * null. 2. When picked the head of the list will be the player! 3. Every
     * other item on the list will be a shroom.
     */
    public Body prev, next;

    private Level level;

    public Mushroom(float x, float y) throws SlickException {
        initShape(x, y);
        initSprite();
        currentStatus = Status.IDLE;
        scale = 1;
        shaded = true;
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/mushroom/mushroom.png");
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, RADIUS);
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public void removeFromLevel(Level l) {
        // don't break a chain of mushrooms
        if (next != null) {
            if (prev instanceof Mushroom) {
                ((Mushroom) prev).next = next;
            }
            ((Mushroom) next).prev = prev;
        }
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.PLAYER) {
            currentStatus = Status.PICKED;
        }
    }

    public void render(Graphics g) {
        if (currentStatus == Status.DEAD) {
            return;
        }
        sprite.draw(getX(), getY(), getWidth(), getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (shaded) {
            timer = 0;
            if (scale < MAX_SCALE) {
                scale += SCALE_INCREMENT;
                resize();
            }
            /* Turn to a monster */
        }
        if (!shaded) {
            if (scale > MIN_SCALE) {
                scale += -SCALE_INCREMENT / 2;
                resize();
            } else {
                level.remove(this);
                currentStatus = Status.DEAD;
            }
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
        // Transform t = Transform.createTranslateTransform(d.x, d.y);
        // shape = shape.transform(t);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
    }

    private void resize() {
        float x = shape.getCenterX();
        float y = shape.getCenterY();
        ((Circle) shape).setRadius(RADIUS * scale);
        shape.setCenterX(x);
        shape.setCenterY(y);

        // // Right way doesn't work due to bug in Slick
        // Transform t = Transform.createScaleTransform(scale, scale);
        // shape = shape.transform(t);
    }

    public Shape castShadow(float direction) {
        // Vector2f d = Geom.calculateVector(2 * depth, direction);
        // Transform t = Transform.createTranslateTransform(d.x, d.y);
        // return shape.transform(t);
        return null;
    }

    public int getZIndex() {
        return 2;
    }

    public int compareTo(ShadowCaster s) {
        return (getZIndex() - s.getZIndex());
    }

}
