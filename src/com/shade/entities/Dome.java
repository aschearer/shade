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
import com.shade.crash.Repelable;
import com.shade.lighting.LuminousEntity;

public class Dome extends Body implements LuminousEntity, Repelable {

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
    public void onCollision(Entity obstacle) {
        // TODO Auto-generated method stub

    }
    
    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
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
        float x = getXCenter();
        float y = getYCenter();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }

    public int getZIndex() {
        return height;
    }

    public void repel(Body b) {
        float playerx = b.getXCenter();
        float playery = b.getYCenter();
        float dist_x = playerx - getXCenter();
        float dist_y = playery - getYCenter();
        float mag = (float) Math.sqrt(dist_x * dist_x + dist_y * dist_y);
        float playradius = b.getWidth() / 2;
        float obstacleradius = getWidth() / 2;
        float angle = (float) Math.atan2(dist_y, dist_x);
        float move = (playradius + obstacleradius - mag) * 1.5f;
        float x_move = (float) (Math.cos(angle) * move);
        float y_move = (float) (Math.sin(angle) * move);
        b.nudge(x_move, y_move);
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
		return Roles.OBSTACLE.ordinal();
	}

	public void removeFromLevel(Level<?> l) {
		// not important for domes
	}

	public int compareTo(LuminousEntity l) {
		return getZIndex() - l.getZIndex();
	}

}
