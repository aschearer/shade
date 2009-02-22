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

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Repelable;
import com.shade.lighting.LuminousEntity;
import com.shade.util.Geom;

public class TransparentFence extends Fence {

    private Image sprite;

    public TransparentFence(int x, int y, int z, int r) throws SlickException {
    	super(x,y,z,r);
        int w = (r == 0) ? 120 : 11;
        int h = (r == 0) ? 11 : 120;
        initShape(x, y, w, h);
        initSprite(w, h);
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    private void initSprite(float w, float h) throws SlickException {
        String path = "entities/fence/fence.transparent.vertical.png";
        if (w > h) {
            path = "entities/fence/fence.transparent.horizontal.png";
        }
        sprite = new Image(path);
    }

    public Shape castShadow(float direction, float depth) {
    	return null;
    }


    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
    }




}
