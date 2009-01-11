package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Repelable;
import com.shade.levels.Model;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Geom;

public class TransparentDoor extends Body implements LuminousEntity, Repelable {

    private enum ActiveSide {
        TOP, RIGHT, BOTTOM, LEFT
    };

    private static Sound open;

    private ActiveSide softspot;
    private int zindex;
    private float luminosity;
    private int times, timer;
    private boolean active;
    private float x, y, width, height;
    private float heading;
    private float xPivot, yPivot;
    private Image door, arrow;

    static {
        try {
            open = new Sound("entities/door/open.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public TransparentDoor(int x, int y, int z, int r) throws SlickException {
        softspot = ActiveSide.values()[r];
        int w = (r == 0 || r == 2) ? 60 : 11;
        int h = (r == 1 || r == 3) ? 60 : 11;
        initShape(x, y, w, h);
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        initPivot(r);
        initResources(r);
        zindex = z;
    }

    private void initResources(int r) throws SlickException {
        if (r == 0 || r == 2) {
            door = new Image("entities/door/door-horizontal-translucent.png");
        }
        if (r == 1 || r == 3) {
            door = new Image("entities/door/door-vertical-translucent.png");
        }
        arrow = new Image("entities/door/arrow.png");
        if (r == 0) {
            arrow.rotate(180);
        }
        if (r == 1) {
            arrow.rotate(-90);
        }
        if (r == 3) {
            arrow.rotate(90);
        }

    }

    private void initPivot(int r) {
        if (r == 0) {
            xPivot = getX() + getWidth();
            yPivot = getYCenter();
        }
        if (r == 1) {
            xPivot = getXCenter();
            yPivot = getY() + getHeight();
        }
        if (r == 2) {
            xPivot = getX();
            yPivot = getYCenter();
        }
        if (r == 3) {
            xPivot = getXCenter();
            yPivot = getY();
        }
    }

    private void initShape(int x, int y, int w, int h) {
        shape = new Rectangle(x, y, w, h);
    }

    public Shape castShadow(float direction, float depth) {
    	return null;
    	/*
        Vector2f v = Geom.calculateVector(getZIndex() * depth, direction);

        Transform t = Transform.createTranslateTransform(v.x, v.y);
        Polygon extent = (Polygon) shape.transform(t);

        int index = 0;

        if (active) {
            float distance = Float.MAX_VALUE;
            for (int i = 0; i < extent.getPointCount(); i++) {
                float[] p = closestPoint(extent.getPoint(i), shape.getPoints());
                float d = Geom.distance2(extent.getPoint(i), p[0], p[1]);
                if (d < distance) {
                    index = i;
                    distance = d;
                }
            }
        } else {
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

        return shade;*/
    }

    /* Find the closest piont in points to s. */
    private float[] closestPoint(float[] s, float[] points) {
        float[] closest = new float[2];
        closest[0] = points[0];
        closest[1] = points[1];
        float distance = Geom.distance2(s[0], s[1], points[0], points[1]);
        for (int i = 2; i < points.length / 2; i++) {
            float d = Geom.distance2(s, points[2 * i], points[2 * i + 1]);
            if (d < distance) {
                closest[0] = points[2 * i];
                closest[1] = points[2 * i + 1];
                distance = d;
            }
        }
        return closest;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public int getZIndex() {
        return zindex;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level<?> l) {
        Model m = (Model) l;
        if (softspot == ActiveSide.TOP) {
            m.add(new Dummy(x + width / 2, y - 20, arrow));
        }
        if (softspot == ActiveSide.RIGHT) {
            m.add(new Dummy(x + width + 20, y + height / 2, arrow));
        }
        if (softspot == ActiveSide.BOTTOM) {
            m.add(new Dummy(x + width / 2, y + height + 20, arrow));
        }
        if (softspot == ActiveSide.LEFT) {
            m.add(new Dummy(x - 20, y + height / 2, arrow));
        }
    }

    public int getRole() {
        return Roles.OBSTACLE.ordinal();
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PLAYER.ordinal() && times > 0) {
            activate();
        }
        if (obstacle.getRole() == Roles.PLAYER.ordinal()) {
            if (softspot == ActiveSide.TOP
                    && getYCenter() > obstacle.getYCenter()) {
                activate();
            }
            if (softspot == ActiveSide.RIGHT
                    && getXCenter() < obstacle.getXCenter()) {
                activate();
            }
            if (softspot == ActiveSide.BOTTOM
                    && getYCenter() < obstacle.getYCenter()) {
                activate();
            }
            if (softspot == ActiveSide.LEFT
                    && getXCenter() > obstacle.getXCenter()) {
                activate();
            }
        }
    }

    private void activate() {
        if (!active) {
            open.play();
        }
        timer = 0;
        active = true;
    }

    public void removeFromLevel(Level<?> l) {
        // TODO Auto-generated method stub

    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(xPivot, yPivot, (float) Math.toDegrees(heading));
        door.draw(x, y, width, height);
        g.resetTransform();
    }

    public void update(StateBasedGame game, int delta) {
        if (game.getContainer().getInput().isKeyPressed(Input.KEY_F)) {
            activate();
        }
        if (active && times < 20) {
            shape = shape.transform(Transform.createRotateTransform(
                    (float) -Math.PI / 40, xPivot, yPivot));
            heading -= Math.PI / 40;
            times++;
        }
        if (times == 20) {
            timer += delta;
            if (timer > 1000) {
                active = false;
            }
        }
        if (!active && times != 0) {
            shape = shape.transform(Transform.createRotateTransform(
                    (float) (Math.PI / 40), xPivot, yPivot));
            heading += Math.PI / 40;
            times--;
        }
    }

    public int compareTo(LuminousEntity o) {
        return getZIndex() - o.getZIndex();
    }

    public void repel(Body b) {
        float velx = b.getXVelocity();
        float vely = b.getYVelocity();
        float playerx = b.getXCenter();
        float playery = b.getYCenter();
        // determine overlap
        float right = playerx - b.getWidth() / 2
                - (getXCenter() + width / 2);
        float left = playerx + b.getWidth() / 2
                - (getXCenter() - width / 2);
        float top = playery - b.getHeight() / 2
                - (getYCenter() + height / 2);
        float bottom = playery + b.getHeight() / 2
                - (getYCenter() - height / 2);
        float minx = Math.min(Math.abs(right), Math.abs(left));
        float miny = Math.min(Math.abs(top), Math.abs(bottom));
        if (specialCase(minx, miny)) {
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

    private boolean specialCase(float minx, float miny) {
        if (active) {
            return minx > miny;
        }
        return minx < miny;
    }

}
