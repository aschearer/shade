package com.shade.crash;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

public class Ray extends Body {

    public Ray(Body one, Body two) {
        shape = new Line(one.getCenterX(), one.getCenterY(), two.getCenterX(),
                two.getCenterY());
    }

    public Ray(Body one, float dx, float dy) {
        shape = new Line(one.getCenterX(), one.getCenterY(), dx, dy, false);
    }

    public Vector2f getDirection() {
        Vector2f d = new Vector2f();
        d.x = ((Line) shape).getDX();
        d.y = ((Line) shape).getDY();
        d.normalise();
        return d;
    }

    public void render(Graphics g) {
        g.draw(shape);
    }

    public void translateX(float x) {
        shape.setCenterX(getCenterX() + x);
    }

    public void translateY(float y) {
        shape.setCenterY(getCenterY() + y);
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public Role getRole() {
        return Role.RAY;
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub

    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub

    }
}
