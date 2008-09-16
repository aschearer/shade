package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
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
        IDLE, PICKED
    };
    
    public boolean shaded;

    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .01f;
    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 1.2f;
    private static final int MAX_DISTANCE = 2500;
    private static final float SPEED = 1.5f;


    private Status currentStatus;
    private float timer;
    private float scale;
    
    private int depth;
    
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
        depth = 2;
        shaded = true;
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/mushroom/mushroom.png");
    }

    private void initShape(float x, float y) {
        shape = new Circle(x - RADIUS, y - RADIUS, RADIUS);
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void addToLevel(Level l) {
        level = l;
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
        sprite.draw(getX(), getY(), getWidth(), getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        if (currentStatus == Status.IDLE && shaded) {
            timer = 0;
            if (scale < MAX_SCALE) {
                scale += SCALE_INCREMENT;
                grow();
            } else {
                System.out.println(scale);
            }
            /* Turn to a monster */
        }
        if (currentStatus == Status.IDLE && !shaded) {
            if (scale > MIN_SCALE) {
                scale += -SCALE_INCREMENT;
                grow();
            } else {
                level.remove(this);
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
        Transform t = Transform.createTranslateTransform(d.x, d.y);
        shape = shape.transform(t);
    }

    private void grow() {
        ((Circle) shape).setRadius(RADIUS * scale);
    }

    public Shape castShadow(float direction) {
        Vector2f d = Geom.calculateVector(2 * depth, direction);
        Transform t = Transform.createTranslateTransform(d.x, d.y);
        return shape.transform(t);
    }

    public int getZIndex() {
        return depth;
    }

    public int compareTo(ShadowCaster s) {
        return (depth - s.getZIndex());
    }

}
