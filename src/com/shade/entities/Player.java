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
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

public class Player extends Linkable implements ShadowCaster {

    private static final float SPEED = 1f;

    private float heading;
    
    private Level level;
    private Image sprite;
    
    public int sunMeter;
    public int mushroomsCollected;
    
    private float dx, dy;

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
//            Body b = (Body) obstacle;
            
            /* Step back no matter what. */
//            float cx = -dx;
//            float cy = -dy;
//            
//            float x = getX();
//            float xc = getCenterX();
//            float w = getWidth();
//            
//            float bx = b.getX();
//            float bxc = b.getCenterX();
//            float bw = b.getWidth();
            
//            if (getX() + getWidth() < b.getX() || 
//                getX() > b.getX() + b.getWidth()) {
//                cy += dy;
//            }
            
            shape.setCenterX(getCenterX() - dx * SPEED);
            shape.setCenterY(getCenterY() - dy * SPEED);
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
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        testAndMove(game.getContainer().getInput(), delta);
    }

    private void testAndMove(Input input, int delta) {
        dx = 0; 
        dy = 0;
        if (input.isKeyDown(Input.KEY_LEFT)) {
            dx--;
            shape.setCenterX(getCenterX() - SPEED);
        }
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            dx++;
            shape.setCenterX(getCenterX() + SPEED);
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            dy--;
            shape.setCenterY(getCenterY() - SPEED);
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            dy++;
            shape.setCenterY(getCenterY() + SPEED);
        }
        
        if (getCenterX() <= 0) {
            shape.setCenterX(799);
        }
        if (getCenterX() > 799) {
            shape.setCenterX(0);
        }
        if (getCenterY() <= 0) {
            shape.setCenterY(599);
        }
        if (getCenterY() > 599) {
            shape.setCenterY(0);
        }
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
