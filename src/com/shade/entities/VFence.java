package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Repelable;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Geom;

public class VFence extends Body implements LuminousEntity, Repelable {

    private int height;
    private Image sprite;

    public VFence(int x, int y, int z, int d) throws SlickException {
        initShape(x, y, 11, 120);
        height = z;
        initSprite(11, 120);
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    private void initSprite(float w, float h) throws SlickException {
        String path = "entities/fence/fence.vertical.png";
        if (w > h) {
            path = "entities/fence/fence.horizontal.png";
        }
        sprite = new Image(path);
    }

    public Shape castShadow(float direction, float depth) {
        Vector2f v = Geom.calculateVector(height * depth, direction);

        Transform t = Transform.createTranslateTransform(v.x, v.y);
        Polygon extent = (Polygon) shape.transform(t);

        int index = 0;

        if (v.y > 0) { // bottom
            if (v.x > 0) { // right
                index = 0;
            } else { // left
                index = 1;
            }
        } else { // top
            if (v.x > 0) { // right
                index = 3;
            } else { // left
                index = 2;
            }
        }

        Polygon shade = new Polygon();

        for (int i = 1; i < 4; i++) {
            int c = (4 + index + i) % 4;
            float[] p = extent.getPoint(c);
            shade.addPoint(p[0], p[1]);
        }

        for (int i = 3; i > 0; i--) {
            int c = (4 + index + i) % 4;
            float[] p = shape.getPoint(c);
            shade.addPoint(p[0], p[1]);
        }

        return shade;
    }

    public void onCollision(Entity obstacle) {

    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {

    }

    public int getZIndex() {
        return height;
    }

    public void repel(Body b) {
        float velx = b.getXVelocity();
        float vely = b.getYVelocity();
        float playerx = b.getXCenter();
        float playery = b.getYCenter();
        // determine overlap
        float right = playerx - b.getWidth() / 2
                - (getXCenter() + getWidth() / 2);
        float left = playerx + b.getWidth() / 2
                - (getXCenter() - getWidth() / 2);
        float top = playery - b.getHeight() / 2
                - (getYCenter() + getHeight() / 2);
        float bottom = playery + b.getHeight() / 2
                - (getYCenter() - getHeight() / 2);
        float minx = Math.min(Math.abs(right), Math.abs(left));
        float miny = Math.min(Math.abs(top), Math.abs(bottom));
        if (minx < miny) {
            // if we move, move AWAY from the block.
            if (Math.abs(playerx - getXCenter() - velx) < Math.abs(playerx
                    - getXCenter()))
                velx = -velx;
            b.nudge(-velx, 0);
        } else {
            if (Math.abs(playery - getYCenter() - vely) < Math.abs(playery
                    - getYCenter())) {
                vely = -vely;
            }
            b.nudge(0, -vely);
        }

    }

    public float getLuminosity() {
        // not important for a fence
        return 0;
    }

    public void setLuminosity(float l) {
        // not important for a fence
    }

    public void addToLevel(Level<?> l) {
        // not important for a fence
    }

    public int getRole() {
        return Roles.OBSTACLE.ordinal();
    }

    public void removeFromLevel(Level<?> l) {
        // not important for a fence
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
