package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;

import com.crash.Body;

public class Sizzle {
	// the lovely source of sparkliness
	private Body origin;
	// displacement from center
	private int x, y;
	// the sparkle itself
	private SpriteSheet sparkle;
	private Animation sizz;
	private int timer;
	private float intensity;

	public Sizzle(Body b, int x, int y) throws SlickException {
		timer = 0;
		origin = b;
		this.x = x;
		this.y = y;
		sparkle = new SpriteSheet("entities/sizzle/sizzle.png", 50, 50);
		sizz = new Animation(sparkle, 100);
		intensity = 0;
	}

	public void update(int delta) {
		timer += delta;
		sizz.update(delta);
		//System.out.println("mrr?");
	}

	public float getIntensity() {
		return intensity;
	}

	public void animate(Graphics g) {
		float posx = origin.getXCenter() - 5 + x;// +origin.getWidth()/2;//-origin.getWidth()/2+x;
		float posy = origin.getY() + y;// -origin.getHeight()/2+y;

		intensity = g.getPixel((int) posx, (int) posy+20).a;
		// TODO: YAY OPENGL CALLS
		if (intensity > 0.6f) {
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			sizz.draw(posx, posy, 10, 20);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
			GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

}
