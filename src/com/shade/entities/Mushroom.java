package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.util.CrashGeom;
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

public class Mushroom extends Linkable implements ShadowCaster {

    private enum Status {
        IDLE, PICKED, DEAD
    };

    public boolean shaded;

    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .005f;
    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 1.2f;
    private static final int MAX_DISTANCE = 2500;
    private static final float SPEED = 1f;

    private Status currentStatus;
    private float scale;

    private Level level;
    
    private Image sprite;

    private Sound sproutSound;

    public Mushroom(float x, float y) throws SlickException {
        initShape(x, y);
        initSprite();
        initSound();
        currentStatus = Status.IDLE;
        scale = MIN_SCALE;
        shaded = true;
        sproutSound.play();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/mushroom/mushroom.png");
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, RADIUS);
    }

    private void initSound() throws SlickException {
        sproutSound = new Sound("entities/mushroom/sprout.ogg");
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public void removeFromLevel(Level l) {
        currentStatus = Status.DEAD;
        // don't break a chain of mushrooms
        if (next != null) {
            prev.next = next;
            next.prev = prev;
        }
        if (prev != null) {
            prev.next = next;
        }
    }

    public void onCollision(Entity obstacle) {
        if (!picked() && obstacle.getRole() == Role.PLAYER) {
            currentStatus = Status.PICKED;
        }
    }

    private boolean picked() {
        return currentStatus == Status.PICKED;
    }

    public void render(Graphics g) {
        if (currentStatus == Status.DEAD) {
            return;
        }
        sprite.draw(getX(), getY(), getWidth(), getHeight());
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        if (currentStatus == Status.DEAD) {
            return;
        }
        
        if (shaded && !tooBig()) {
            scale += SCALE_INCREMENT;
            resize();
        }

        if (tooBig()) {
            /* TODO Turn to a monster. */
        }
        
        if (!shaded && !tooSmall()) {
            scale += -SCALE_INCREMENT / 2;
            resize();
        }
        
        if (tooSmall()) {
            currentStatus = Status.DEAD;
            level.remove(this);
            return; // Stop execution here
        }
        
        if (picked() && tooFar()) {
            float angle = CrashGeom.calculateAngle(prev, this);
            move(SPEED, angle);
        }
    }

    private boolean tooFar() {
        return CrashGeom.distance2(prev, this) > MAX_DISTANCE;
    }

    private boolean tooSmall() {
        return scale < MIN_SCALE;
    }

    private boolean tooBig() {
        return scale > MAX_SCALE;
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
