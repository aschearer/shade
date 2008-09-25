package com.shade.entities;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.controls.MushroomCounter;
import com.shade.shadows.ShadowCaster;

public class Player extends Linkable implements ShadowCaster {

    private static final float SPEED = 1f;
    
    private Level level;
    private Image sprite;
    
    public boolean shaded;
    private float dx, dy;

    private LinkedList<MushroomCounter> counters;

    public Player(float x, float y, float r) throws SlickException {
        initShape(x, y, r);
        initSprite();
        counters = new LinkedList<MushroomCounter>();
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
        collectMushrooms(obstacle);
        /* Or... */
        moveOutOfIntersection(obstacle);
    }

    private void moveOutOfIntersection(Entity obstacle) {
        if (obstacle.getRole() == Role.OBSTACLE) {
            shape.setCenterX(getCenterX() - dx * SPEED);
            shape.setCenterY(getCenterY() - dy * SPEED);
        }
    }

    private void collectMushrooms(Entity obstacle) {
        if (obstacle.getRole() == Role.BASKET && next != null) {
            notifyCounters();
            
            while (next != null) {
                level.remove(next);
                next.detach();
            }
        }
    }

    private void notifyCounters() {
        for (MushroomCounter c : counters) {
            c.onCollect((Mushroom) next);
        }
    }

    public void render(Graphics g) {
        sprite.drawCentered(getCenterX(), getCenterY());
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        testAndMove(game.getContainer().getInput(), delta);
        testAndWrap();
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
    }

    public Shape castShadow(float direction) {
//        Vector2f d = Geom.calculateVector(5 * getZIndex(), direction);
//        Transform t = Transform.createTranslateTransform(d.x, d.y);
//        return shape.transform(t);
        return null;
    }

    public int getZIndex() {
        return 3;
    }

    public int compareTo(ShadowCaster s) {
        return getZIndex() - s.getZIndex();
    }

    public void add(MushroomCounter counter) {
        counters.add(counter);
    }

}
