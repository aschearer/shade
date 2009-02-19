package com.shade.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.util.Geom;

public class Fence extends Obstacle {

    public Fence(int x, int y, int z, int r) throws SlickException {
        int w = (r == 0) ? 120 : 11;
        int h = (r == 0) ? 11 : 120;
        initShape(x, y, w, h);
        zindex = z;
        initSprite(w, h);
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
        Vector2f v = Geom.calculateVector(zindex * depth, direction);

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
    
    public void update(StateBasedGame game, int delta) {

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

}
