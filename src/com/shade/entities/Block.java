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

public class Block extends Body implements ShadowCaster {

    private Image sprite;
    private int depth;
    private Level level;

    public Block(float x, float y, float w, float h, int d) throws SlickException {
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

    public Shape castShadow(float direction) {
        Vector2f v = Geom.calculateVector(depth * 10, direction);

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

    public void addToLevel(Level l) {
        level = l;
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
        sprite.draw(getX(), getY(), getWidth(), getHeight());
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

}
