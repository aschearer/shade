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

public class Dome extends Obstacle {

    public Dome(int x, int y, int z, int d) throws SlickException {
        initShape(x, y, d);
        zindex = z;
        initSprite();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/dome/dome.png");
    }

    private void initShape(int x, int y, int r) {
        shape = new Circle(x, y, r);
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {

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
        float h = zindex * depth * 1.6f;
        float x = getXCenter();
        float y = getYCenter();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        // TODO cache the rectangle and just rotate it
        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
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

}
