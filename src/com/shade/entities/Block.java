package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.util.Geom;

public class Block extends ShadowCaster {

    private Image sprite;

    public Block(float x, float y, float w, float h, float d) throws SlickException {
        initShape(x, y, w, h);
        initSprite();
        depth = d;
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/block/block.png");
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    @Override
    public void castShadow(float direction) {
        Vector2f v = Geom.calculateVector(depth * 10, direction);

        Transform t = Transform.createTranslateTransform(v.x, v.y);
        Polygon extent = (Polygon) shape.transform(t);
        
        /*
         * Step clockwise through the projected shape. Step counter clockwise
         * through the body's shape. For each pair of points, measure the
         * distance between the pair. Remove the pair which form the shortest
         * line.
         */
        
        int index1 = 0;
        int index2 = 0;
        float d = -1;
        
        for (int i = 0; i < extent.getPointCount(); i++) {
            float p1[] = extent.getPoint(i);
            float p2[] = shape.getPoint((4 - i) % 4);
            
            float nd = Geom.distance2(p1[0], p1[1], p2[0], p2[0]);
            if (d < 0 || d > nd) {
                index1 = i;
                index2 = (4 - i) % 4;
                d = nd;
            }
        }
        
        Polygon shade = new Polygon();
        
        
        for (int i = 1; i < 4; i++) {
            int c = (4 + index1 + i) % 4;
            float[] p = extent.getPoint(c);
            shade.addPoint(p[0], p[1]);
        }
        
        for (int i = 1; i < 4; i++) {
            int c = (4 + index2 + i) % 4;
            float[] p = shape.getPoint(c);
            shade.addPoint(p[0], p[1]);
        }
        
        shadow = shade;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub

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

    public void render(Graphics g) {
        renderShadow(g);
        sprite.draw(getX(), getY(), getWidth(), getHeight());
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub

    }

}
