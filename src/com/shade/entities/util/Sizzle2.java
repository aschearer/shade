package com.shade.entities.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;

import com.crash.Body;
import com.shade.states.MasterState;

public class Sizzle2 {
	public static final float MAX_SCALEUP = 0.5f;
	public static final int MAX_SPARKLES = 50;
	public static final float increment = 0.002f;

	private ArrayList<Sizz> sparkles;
	// the lovely source of sparkliness
	private Body origin;
	// the sparkle itself
	private int timer, timeInSun;
	private float intensity;
	private int x,y;
	private int sparkleCount;
	private String source;
	
	
	public Sizzle2(Body b, int count, int x, int y, String image) throws SlickException {
		timer = 0;
		sparkleCount = count;
		origin = b;
		this.x = x;
		this.y = y;
		source = image;
		initSparkles();
	}
	
	public int getCount(){
		return sparkleCount;
	}

	public void changeCount(int newCount){
		sparkleCount = Math.min(newCount, MAX_SPARKLES);
		timer = 0;
		try{
		while(sparkles.size()<sparkleCount){
			float[] p = getPoint();
			sparkles.add(new Sizz(p[0],p[1], MAX_SCALEUP, increment,
					(int)0, source, this));
		}
		}catch(Exception e){
			
		}
	}
	private void initSparkles() throws SlickException {
		sparkles = new ArrayList<Sizz>();
		for (int i = 0; i < sparkleCount; i++) {
			float[] p = getPoint();
			sparkles.add(new Sizz(p[0],p[1], MAX_SCALEUP, increment,
					(int)(MAX_SCALEUP/increment*i/sparkleCount), source, this));
		}
	}
	
	public float getLuminosity (){
		return intensity;
	}

	public void update(int delta) {
		timer+=delta;
		for(int i = 0;i<sparkles.size();i++){
			sparkles.get(i).update(delta);
		}
		if (intensity > MasterState.SHADOW_THRESHOLD) {
			timeInSun+=delta;
		}
		else timeInSun = 0;
		changeCount(timeInSun/200);
	}
	
	public void animate(Graphics g) {
		//TODO: YAY OPENGL CALLS
		float posx = origin.getXCenter()-1.5f + x;// +origin.getWidth()/2;//-origin.getWidth()/2+x;
		float posy = origin.getYCenter() + y;// -origin.getHeight()/2+y;
		
		intensity = g.getPixel((int) posx-x/5, (int) posy+1-y/5).a;
		
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for(int i = 0;i<sparkles.size();i++){
			sparkles.get(i).draw();
		}
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	public void resetTimer(){
		timeInSun = 0;
		sparkles.clear();
	}
	
	public float[] getPoint(){
		float posx = origin.getXCenter()-1.5f + x;// +origin.getWidth()/2;//-origin.getWidth()/2+x;
		float posy = origin.getYCenter() + y;// -origin.getHeight()/2+y;
		
		if(sparkles.size()>sparkleCount) sparkles.remove(sparkles.size()-1);
		// add a new sparkle
		return new float[]{posx, posy};
	}



}
