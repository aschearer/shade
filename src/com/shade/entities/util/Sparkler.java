package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import com.crash.Body;

public class Sparkler {
	public static final int MAX_SPARKLES = 4;
	public static final float MAX_SCALEUP = 0.5f;
	public static final float increment = 0.001f;

	private Sparkle[] sparkles;
	// the lovely source of sparkliness
	private Body origin;
	// the sparkle itself
	private SpriteSheet sparkle;
	private int timer;
	
	public Sparkler(Body b) {
		timer = 0;
		origin = b;
		initSparkles();
		for (int i = 0; i < sparkles.length; i++) {
			float[] p = getPoint();
			sparkles[i] = new Sparkle(p[0],p[1], this);
		}
		try {
			sparkle = new SpriteSheet("res/entities/sparkle/spark.png", 50, 50);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("whoa damn you're screwed");
		}
	}

	private void initSparkles() {
		sparkles = new Sparkle[MAX_SPARKLES];
	}

	public void update(int delta) {
		timer+=delta;
		for(int i = 0;i<sparkles.length;i++){
			if(timer>MAX_SCALEUP/increment*i/sparkles.length)
			sparkles[i].update(delta);
		}
	}
	
	public void animate(Graphics g) {
		for(int i = 0;i<sparkles.length;i++){
			if(timer>MAX_SCALEUP/increment*i/sparkles.length)
			sparkles[i].draw();
		}
	}
	
	public float[] getPoint(){
		// add a new sparkle
		float source_x = origin.getXCenter() - origin.getWidth()/3
				+ (float) Math.random() * origin.getWidth()/3*2;
		float source_y = origin.getYCenter() - origin.getHeight()/3
				+ (float) Math.random() * origin.getHeight()/3*2;
		return new float[]{source_x,source_y};
	}


	// A point class
	private class Sparkle {
		float x;
		float y;
		private SpriteSheet image;
		private float scale;
		private Sparkler daddy;

		public Sparkle(float x, float y, Sparkler s) {
			daddy = s;
			this.x = x;
			this.y = y;
			try{
				image = new SpriteSheet("res/entities/sparkle/spark.png",
						20, 20);
			}
			catch (Exception e){
				
			}
		}
		
		public void draw(){
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			image.draw(x-sparkle.getWidth()/2*scale, y-sparkle.getHeight()/2*scale, scale);
			//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
		}
		
		public void update(int delta){
			if(scale<MAX_SCALEUP){
				scale += (float)delta*increment;
			}
			else renewPoint();
		}
		private void renewPoint(){
			scale = 0.1f;
			float[] newPoint = daddy.getPoint();
			x = newPoint[0];
			y = newPoint[1];
		}
	}
}
