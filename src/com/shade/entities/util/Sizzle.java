package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
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
		sparkle = new SpriteSheet("entities/sizzle/sizzle.png", 50, 60);
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
		float posx = origin.getXCenter()-1.5f + x;// +origin.getWidth()/2;//-origin.getWidth()/2+x;
		float posy = origin.getY()-4 + y;// -origin.getHeight()/2+y;
		intensity = g.getPixel((int) posx-x/5, (int) posy+20+2-y/5).a;
		// TODO: YAY OPENGL CALLS
		if (intensity > 0.6f) {
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			sizz.draw(posx-2.5f, posy+5f,10,20);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
			GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			g.setColor(Color.red);
			//g.fillOval((int) posx-x/5, (int) posy+20-y/5,5,5);
		}
		else {
			g.setColor(Color.blue);
		//	g.fillOval((int) posx-x/5, (int) posy+20-y/5,5,5);
		
		}
	}

}
