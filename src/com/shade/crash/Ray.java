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

    private float heading;

    public Ray(Body one, Body two) {
        shape = new Rectangle(one.getCenterX() - 4, one.getCenterY(), 8,
                CrashGeom.distance(one, two));
        heading = CrashGeom.calculateAngle(one, two);
        Transform t = Transform.createRotateTransform(heading,
                                                      one.getCenterX(), one
                                                              .getCenterY());
        shape = shape.transform(t);
    }

    public Vector2f getDirection() {
        return Geom.calculateVector(1, heading);
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
