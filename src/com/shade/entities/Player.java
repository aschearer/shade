package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

public class Player extends Linkable implements ShadowCaster {

    private static final float SPEED = 1.2f;
    /* In radians... */
    private static final float TORQUE = .05f;

    private float heading;
    private float dx, dy;
    
    private Level level;
    private Image sprite;
    
    public int mushroomsCollected;

    public Player(float x, float y, float r) throws SlickException {
        initShape(x, y, r);
        initSprite();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/player/player.png");
    }

    private void initShape(float x, float y, float r) {
        shape = new Circle(x, y, r);
    }

    public Role getRole() {
        return Role.PLAYER;
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
    }
    
    public void onCollision(Entity obstacle) {
        pickMushroom(obstacle);
        /* Or... */
        collectMushrooms(obstacle);
        /* Or... */
        moveOutOfIntersection(obstacle);
    }

    private void moveOutOfIntersection(Entity obstacle) {
        if (obstacle.getRole() == Role.OBSTACLE) {
            Body b = (Body) obstacle;
            
            /* Step back no matter what. */
            float cx = -dx;
            float cy = -dy;
            
//            float x = getX();
//            float xc = getCenterX();
//            float w = getWidth();
//            
//            float bx = b.getX();
//            float bxc = b.getCenterX();
//            float bw = b.getWidth();
            
            if (getX() + getWidth() < b.getX() || 
                getX() > b.getX() + b.getWidth()) {
                cy += dy;
            }
            
            Transform t = Transform.createTranslateTransform(cx, cy);
            shape = shape.transform(t);
        }
    }

    private void collectMushrooms(Entity obstacle) {
        if (obstacle.getRole() == Role.BASKET && next != null) {
            Linkable head = next;
            while (head.next != null) {
                Linkable m = head;
                head = head.next;
                m.prev = null; /* Kill the node. */
                m.next = null;
                level.remove(m);
                mushroomsCollected++;
            }
            /* Kill the last damn mushroom. */
            next = null;
            head.prev = null;
            level.remove(head);
            mushroomsCollected++;
        }
    }

    private void pickMushroom(Entity obstacle) {
        if (obstacle.getRole() == Role.MUSHROOM) {
            Linkable m = (Linkable) obstacle;
            if (next == null) { /* First shroom picked. */
                next = m;
                next.prev = this;
                return; /* Exit, we're finished. */
            }
            Linkable head = next;
            /* Cycle through list and add to end, watch out for duplicates. */
            while (!head.equals(m) && head.next != null) {
                head = (Linkable) head.next;
            }
            if (m.equals(head)) {
                return; /* Exit we're not dealing with a duplicate. */
            }
            head.next = m;
            m.prev = head;
        }
    }

    public void render(Graphics g) {
        g.rotate(getCenterX(), getCenterY(), (float) Math.toDegrees(heading));
        sprite.drawCentered(getCenterX(), getCenterY());
        g.resetTransform();
    }

    public void update(StateBasedGame game, int delta) {
        testAndMove(game.getContainer().getInput(), delta);
    }

    private void testAndMove(Input input, int delta) {
        if (input.isKeyDown(Input.KEY_LEFT)) {
            rotate(-TORQUE);
        }
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            rotate(TORQUE);
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            move(SPEED, heading);
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            move(-SPEED, heading);
        }
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        dx = d.x;
        dy = d.y;
        Transform t = Transform.createTranslateTransform(d.x, d.y);
        shape = shape.transform(t);
    }

    private void rotate(float radians) {
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(radians, x, y);
        shape = shape.transform(t);
        heading += radians;
    }

    public Shape castShadow(float direction) {
        Vector2f d = Geom.calculateVector(5 * getZIndex(), direction);
        Transform t = Transform.createTranslateTransform(d.x, d.y);
        return shape.transform(t);
    }

    public int getZIndex() {
        return 3;
    }

    public int compareTo(ShadowCaster s) {
        return getZIndex() - s.getZIndex();
    }

}
