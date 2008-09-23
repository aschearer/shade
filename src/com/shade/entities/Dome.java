package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.shadows.ShadowCaster;

public class Dome extends Body implements ShadowCaster {

    private Image sprite;
    private int depth;

    public Dome(float x, float y, float r, int d) throws SlickException {
        initShape(x, y, r);
        depth = d;
        initSprite();
    }

    private void initSprite() throws SlickException {
        String path = "entities/dome/dome.small.png";
        if (depth > 4) {
            path = "entities/dome/dome.medium.png";
        }
        if (depth > 6) {
            path = "entities/dome/dome.big.png";
        }
        if (depth > 10) {
            path = "entities/dome/dome.xbig.png";
        }
        
        sprite = new Image(path);
    }

    private void initShape(float x, float y, float r) {
        shape = new Circle(x, y, r);
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
        sprite.draw(getX(), getY(), getWidth(), getHeight());
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub

    }

    /**
     * Return a round rectangle as the shadow.
     * 
     * Note that this means there is some shadow underneath the dome. This
     * obviously will impact the odds of a mushroom being placed in said shadow.
     * But it's performant so I'm willing to accept that.
     */
    public Shape castShadow(float direction) {
        float r = ((Circle) shape).radius;
        float h = depth * 10 * 2;
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }

    public int getZIndex() {
        return depth;
    }

    public int compareTo(ShadowCaster s) {
        return depth - s.getZIndex();
    }

}
