package com.shade.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.util.Geom;

public class Block extends Body {
    
    private static final float H_WIDTH = 40;
    private static final float WIDTH = 80;
    private static final float H_HEIGHT = 40;
    private static final float HEIGHT = 80;
    
    private int depth;
    private float heading;
    
    private boolean dirty;
    private Shape shadow;

    public Block(float x, float y) {
        initShape(x, y);
        depth = 60;
        dirty = true;
    }
    
    public Block(float x, float y, float r) {
        this(x, y);
        r = (r == .5f) ? .51f : r; 
        rotate(r);
        heading = r;
    }
    
    private void rotate(float r) {
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(r, x, y);
        shape = shape.transform(t);
    }

    private void initShape(float x, float y) {
        shape = new Rectangle(x - H_WIDTH, y - H_HEIGHT, WIDTH, HEIGHT);
    }
    
    private void calcShadow() {
        shadow = shape;
        for (int i = 1; i < depth; i++) {
            Vector2f d = Geom.calculateVector(i, .5f);
            Transform t = Transform.createTranslateTransform(d.x, d.y);
            Shape[] union = shadow.union(shape.transform(t));
            shadow = union[0];
        }
    }
    
    public Role getRole() {
        return Role.OBSTACLE;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub
        
    }

    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub
        
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub
        
    }
    
    public float getHeading() {
        return heading;
    }

    public void render(Graphics g) {
        if (dirty) {
            calcShadow();
            dirty = false;
        }
        
        Color c = g.getColor();
        Color s = Color.darkGray;
        s.a = .5f;
        g.setColor(s);
        
        g.fill(shadow);
        
        g.setColor(Color.white);
        g.fill(shape);
        g.setColor(c);
    }

    public void update(StateBasedGame game, int delta) {
    }

}
