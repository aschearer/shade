package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.util.Geom;

public class Player implements Entity {
    
    private static final float SPEED = 4f;
    /* In radians... */
    private static final float TORQUE = .1f;
    
    private float heading;
    private Shape shape;
    
    public Player(float x, float y) {
        initShape(x, y);
    }

    private void initShape(float x, float y) {
        Polygon p = new Polygon();
        p.addPoint(-15f, 0);
        p.addPoint(15f, 0);
        p.addPoint(0, 40f);
        p.setLocation(x, y);
        shape = p;
    }

    public Role getRole() {
        return Role.PLAYER;
    }
    
    public void addToLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub

    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void render(Graphics g) {
        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        testAndMove(game.getContainer().getInput(), delta);
    }

    private void testAndMove(Input input, int delta) {
        if (input.isKeyDown(Input.KEY_LEFT)) {
            shape.transform(Transform.createRotateTransform(-TORQUE));
            heading += -TORQUE;
        }
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            shape.transform(Transform.createRotateTransform(TORQUE));
            heading += TORQUE;
        }
        if (input.isKeyDown(Input.KEY_UP)) {
            Vector2f d = Geom.calculateVector(SPEED, heading);
            shape.transform(Transform.createTranslateTransform(d.x, d.y));
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            Vector2f d = Geom.calculateVector(-SPEED, heading);
            shape.transform(Transform.createTranslateTransform(d.x, d.y));
        }
    }

}
