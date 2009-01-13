package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;

import com.crash.Body;

public class PlayerSparkler {
	public static final float MAX_SCALEUP = 0.5f;
	public static final float increment = 0.001f;

	private ArrayList<Sparkle> sparkles;
	// the lovely source of sparkliness
	private Body origin;
	// the sparkle itself
	private int timer;
	private int sparkleCount;
	private String source;
	
	
	public PlayerSparkler(Body b, int count, String image) throws SlickException {
		timer = 0;
		sparkleCount = count;
		origin = b;
		source = image;
		initSparkles();
	}
	
	public int getCount(){
		return sparkleCount;
	}

	public void changeCount(int newCount){
		sparkleCount = newCount;
		timer = 0;
		try{
		while(sparkles.size()<sparkleCount){
			float[] p = getPoint();
			sparkles.add(new Sparkle(p[0],p[1], MAX_SCALEUP, increment,
					(int)0, source, this));
		}
		}catch(Exception e){
			
		}
	}
	private void initSparkles() throws SlickException {
		sparkles = new ArrayList<Sparkle>();
		for (int i = 0; i < sparkleCount; i++) {
			float[] p = getPoint();
			sparkles.add(new Sparkle(p[0],p[1], MAX_SCALEUP, increment,
					(int)(MAX_SCALEUP/increment*i/sparkleCount), source, this));
		}
	}

	public void update(int delta) {
		timer+=delta;
		for(int i = 0;i<sparkles.size();i++){
			sparkles.get(i).update(delta);
		}
	}
	
	public void animate(Graphics g) {
		//TODO: YAY OPENGL CALLS
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for(int i = 0;i<sparkles.size();i++){
			sparkles.get(i).draw();
		}
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	public float[] getPoint(){
		if(sparkles.size()>sparkleCount) sparkles.remove(sparkles.size()-1);
		// add a new sparkle
		float source_x = origin.getXCenter() - origin.getWidth()/3
				+ (float) Math.random() * origin.getWidth()/3*2;
		float source_y = origin.getYCenter() - origin.getHeight()/3
				+ (float) Math.random() * origin.getHeight()/3*2;
		return new float[]{source_x,source_y};
	}



}
