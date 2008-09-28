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

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

/**
 * A simple block which can cast a shadow.
 * 
 * Not much to see here. The shadow implementation is pretty clever though if I
 * do say so myself. It projects the block a certain distance based on its depth
 * and then creates a new polygon with three points from the shadow and block.
 * 
 * The shadow algorithm is clearly fast. I have had success running it on each
 * frame! Even with 16-20 blocks and dozens of mushrooms there has been no
 * problems.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class Block extends Body implements ShadowCaster {

    private Image sprite;
    private int depth;

    public Block(float x, float y, float w, float h, int d)
            throws SlickException {
        initShape(x, y, w, h);
        depth = d;
        initSprite();
    }

    private void initSprite() throws SlickException {
        String path = "entities/block/block.small.png";
        if (depth > 6) {
            path = "entities/block/block.medium.png";
        }
        if (depth > 10) {
            path = "entities/block/block.big.png";
        }

        sprite = new Image(path);
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    /**
     * Determine which corner points between the block and its shadow are
     * closest and then build a new polygon from the resulting points.
     */
    public Shape castShadow(float direction) {
        Vector2f v = Geom.calculateVector(depth * 10, direction);

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
     * the block furthest from the shadow. One can be derived from the other.
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

    public void addToLevel(Level l) {

    }

    public Role getRole() {
        return Role.OBSTACLE;
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub

    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub

    }

    public int getZIndex() {
        return depth;
    }

    public int compareTo(ShadowCaster s) {
        return (depth - s.getZIndex());
    }

    public void repel(Entity repellee) {
        Body b = (Body) repellee;
        Vector2f vect = b.getVelocity();
        double velx = vect.x;
        double vely = vect.y;
        double playerx = b.getCenterX();
        double playery = b.getCenterY();
        // determine overlap
        double right = playerx - b.getWidth() / 2
                - (getCenterX() + getWidth() / 2);
        double left = playerx + b.getWidth() / 2
                - (getCenterX() - getWidth() / 2);
        double top = playery - b.getHeight() / 2
                - (getCenterY() + getHeight() / 2);
        double bottom = playery + b.getHeight() / 2
                - (getCenterY() - getHeight() / 2);
        double minx = Math.min(Math.abs(right), Math.abs(left));
        double miny = Math.min(Math.abs(top), Math.abs(bottom));
        if (minx < miny) {
            b.move(-velx, 0);
        } else {
            b.move(0, -vely);
        }
    }

}
