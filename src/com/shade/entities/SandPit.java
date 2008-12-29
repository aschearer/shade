package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.lighting.LuminousEntity;

public class SandPit extends Body implements LuminousEntity {

    private float luminosity;
    private Image sprite;
    
    public SandPit(int x, int y, int d, int o) throws SlickException {
        if (o == 0) {
            initCircle(x, y, d);
        } else {
            initSquare(x, y, d, d);
        }
    }

    private void initSquare(int x, int y, int w, int h) throws SlickException {
        shape = new Rectangle(x, y, w, h);
        sprite = new Image("entities/sandpit/square.png");
    }

    private void initCircle(int x, int y, int r) throws SlickException {
        shape = new Circle(x, y, r);
        sprite = new Image("entities/sandpit/circle.png");
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public int getZIndex() {
        return 0;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level<?> l) {
        
    }

    public int getRole() {
        return Roles.SANDPIT.ordinal();
    }

    public void onCollision(Entity obstacle) {
        
    }

    public void removeFromLevel(Level<?> l) {
        
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight() );
//        g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        
    }

    public int compareTo(LuminousEntity o) {
        return getZIndex() - o.getZIndex();
    }

}
