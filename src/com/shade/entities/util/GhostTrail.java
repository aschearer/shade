package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;

import com.crash.Body;

public class GhostTrail {
	// the lovely source of sparkliness
	private Body origin;
	// displacement from center
	// the sparkle itself
	private Ghost[] ghosties;
	private float[] alpha;
	private int timer;

	public GhostTrail(Body b, String source) throws SlickException {
		timer = 0;
		origin = b;
		ghosties = new Ghost[10];
		for (int i = 0; i < ghosties.length; i++) {
			ghosties[i] = new Ghost(this, source);
		}
	}

	public void update(int delta) {
		timer += delta;
		ghosties[0].refresh(getOriginX(), getOriginY());
		for (int i = ghosties.length-1; i>0; i--) {
				ghosties[i].update(delta);
				if(ghosties[i].timer>Ghost.GHOST_LIFESPAN)
				ghosties[i].refresh(ghosties[i-1].x, ghosties[i-1].y);
		}

		// System.out.println("mrr?");
	}

	public float getOriginX() {
		return origin.getXCenter();
	}

	public float getOriginY() {
		return origin.getYCenter();
	}

	public void animate(Graphics g) {
		// TODO: YAY OPENGL CALLS
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (int i = ghosties.length-1; i > 0; i--) {
			ghosties[i].draw();
		}
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}

	private class Ghost {
		public static final int GHOST_LIFESPAN = 10;
		public static final float MAX_SIZE = 1f;
		float x;
		float y;
		float alpha;
		Image image;
		int timer;
		GhostTrail trail;

		public Ghost(GhostTrail dad, String s) {
			trail = dad;
			try {
				image = new Image(s);
			} catch (Exception e) {

			}
		}

		public void draw() {
			image.draw(x-image.getWidth()/2, y-image.getHeight()/2, new Color(1f,1f,1f,alpha));
		}

		public void update(int delta) {
			timer += delta;
			alpha = MAX_SIZE*(1f - 1.0f * timer / GHOST_LIFESPAN);
		}

		public void refresh(float x, float y) {
			this.x = x;
			this.y = y;
			alpha = MAX_SIZE;
			timer = 0;
		}
	}

}
