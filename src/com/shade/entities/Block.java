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

public class Block extends Obstacle {

    public Block(int x, int y, int z, int d) throws SlickException {
        initShape(x, y, d, d);
        zindex = z;
        initSprite();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/block/block.png");
    }

    private void initShape(int x, int y, int w, int h) {
        shape = new Rectangle(x, y, w, h);
    }

    /**
     * Determine which corner points between the block and its shadow are
     * closest and then build a new polygon from the resulting points.
     */
    public Shape castShadow(float direction, float depth) {
        Vector2f v = Geom.calculateVector(zindex * depth, direction);

        Transform t = Transform.createTranslateTransform(v.x, v.y);
        Polygon extent = (Polygon) shape.transform(t);

        int index = findKeyPoint(v);

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

    /**
     * Given two rectangles, a block and its shadow, the key point is the corner
     * on the shadow closest to the block. The second key point is the corner on
     * the block furtherst from the shadow. One can be derived from the other.
     * 
     * @param v
     * @return
     */
    private int findKeyPoint(Vector2f v) {
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
        return index;
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

    public void update(StateBasedGame game, int delta) {

    }
}
