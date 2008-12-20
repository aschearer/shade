package com.shade.entities;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.util.Geom;

public class MovingDome extends Dome {
    
    private static final float SPEED = 2f; 
    
    private float startX, startY;
    private float endX, endY;
    private float heading;
    private float speed;

    public MovingDome(int x, int y, int x2, int y2, int z, int d) throws SlickException {
        super(x, y, z, d);
        startX = x;
        startY = y;
        endX = x2;
        endY = y2;
        heading = Geom.calculateAngle(endX, endY, startX, startY);
        speed = SPEED;
    }
    
    @Override
    public void update(StateBasedGame game, int delta) {
        
        Vector2f v = Geom.calculateVector(speed, heading);
        nudge(v.x, v.y);
        
        if (getXCenter() == endX && getYCenter() == endY) {
            speed = -speed;
        }
//        if (getXCenter() == startX && getYCenter() == startY) {
//            speed = -speed;
//        }
    }

}
