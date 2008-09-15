package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.util.Geom;

public class Player extends Body {

    private static final float SPEED = 1.5f;
    /* In radians... */
    private static final float TORQUE = .05f;

    private float heading;
    
    private Level level;
    private Mushroom shroomie;
    private Image sprite;

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
        if (obstacle.getRole() == Role.MUSHROOM) {
            Mushroom m = (Mushroom) obstacle;
            if (shroomie == null) { /* First shroom picked. */
                shroomie = m;
                shroomie.prev = this;
                return; /* Exit, we're finished. */
            }
            Mushroom head = shroomie;
            /* Cycle through list and add to end, watch out for duplicates. */
            while (!head.equals(m) && head.next != null) {
                head = (Mushroom) head.next;
            }
            if (m.equals(head)) {
                return; /* Exit we're not dealing with a duplicate. */
            }
            head.next = m;
            m.prev = head;
        }
        

        if (obstacle.getRole() == Role.BASKET && shroomie != null) {
            Mushroom head = shroomie;
            while (head.next != null) {
                Mushroom m = head;
                head = (Mushroom) head.next;
                m.prev = null; /* Kill the node. */
                m.next = null;
                level.remove(m);
            }
            /* Kill the last damn mushroom. */
            shroomie = null;
            head.prev = null;
            level.remove(head);
        }
        
        if (obstacle.getRole() == Role.OBSTACLE) {
            // TODO improve this shitty approach.
            move(-SPEED, heading);
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

}
