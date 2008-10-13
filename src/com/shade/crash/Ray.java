package com.shade.crash;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.util.CrashGeom;
import com.shade.util.Geom;

public class Ray extends Body {

    private Vector2f start, end;

    public Ray(Body one, Body two) {
        float w = one.getWidth();
        shape = new Rectangle(one.getCenterX() - w / 2, one.getCenterY(), w,
                CrashGeom.distance(one, two));
        start = new Vector2f(one.getCenterX(), one.getCenterY());
        end = new Vector2f(two.getCenterX(), two.getCenterY());
        float heading = Geom.calculateAngle(start.x, start.y, end.x, end.y);
        Transform t = Transform.createRotateTransform(heading,
                                                      one.getCenterX(), 
                                                      one.getCenterY());
        shape = shape.transform(t);
    }

    public Vector2f getDirection() {
        Vector2f d = new Vector2f();
        d.x = end.x - start.x;
        d.y = end.y - start.y;
        return d.normalise();
    }

    public void render(StateBasedGame game, Graphics g) {
        g.draw(shape);
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

    public void repel(Entity repellee) {
        // TODO Auto-generated method stub

    }
}
