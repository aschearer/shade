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
    private int height;

    public Dome(float x, float y, float r, int d) throws SlickException {
        initShape(x, y, r);
        height = d;
        initSprite();
    }

    private void initSprite() throws SlickException {
        String path = "entities/dome/dome.small.png";
        if (height > 6) {
            path = "entities/dome/dome.medium.png";
        }
        if (height > 9) {
            path = "entities/dome/dome.big.png";
        }
        if (height > 12) {
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
    public Shape castShadow(float direction, float depth) {
        float r = ((Circle) shape).radius;
        float h = height * depth * 1.6f;
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }

    public int getZIndex() {
        return height;
    }

    public int compareTo(ShadowCaster s) {
        return height - s.getZIndex();
    }

	public void repel(Entity repellee) {
		Body b = (Body) repellee;
		double playerx = b.getCenterX();
		double playery = b.getCenterY();
		double dist_x = playerx-getCenterX();
		double dist_y = playery-getCenterY();
		double mag = Math.sqrt(dist_x*dist_x + dist_y*dist_y);
		double playradius = b.getWidth()/2;
		double obstacleradius = getWidth()/2;
		double angle = Math.atan2(dist_y,dist_x);
		double move = (playradius+obstacleradius-mag)*1.5;
		b.move(Math.cos(angle)*move,Math.sin(angle)*move);
	}

}
