package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.lighting.LuminousEntity;

public class Umbrella extends Body implements LuminousEntity {

    private static final int RADIUS = 28;
    private static final int DEPTH = 5;
    private Image sprite;
    private int height;

    public Umbrella(int x, int y) throws SlickException {
        initShape(x, y, RADIUS);
        height = DEPTH;
        initSprite();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/umbrella/umbrella.png");
    }

    private void initShape(int x, int y, int r) {
        shape = new Circle(x, y, r);
    }

    public void onCollision(Entity obstacle) {

    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2);
        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {

    }

    /**
     * Return a round rectangle as the shadow.
     * 
     * Note that this means there is some shadow underneath the dome. This
     * obviously will impact the odds of a mushroom being placed in said shadow.
     * But it's performant so I'm willing to accept that.
     */
    public Shape castShadow(float direction, float depth) {
        float r = ((Circle) shape).radius;
        float h = height * depth * 1.6f;
        float x = getXCenter();
        float y = getYCenter();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        // TODO cache the rectangle and just rotate it
        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }

    public int getZIndex() {
        return height;
    }

    public float getLuminosity() {
        return 0; // not important for domes
    }

    public void setLuminosity(float l) {
        // not important for domes
    }

    public void addToLevel(Level<?> l) {
        // not important for domes
    }

    public int getRole() {
        return Roles.DUMMY.ordinal();
    }

    public void removeFromLevel(Level<?> l) {
        // not important for domes
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
