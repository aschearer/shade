package com.shade.entities.util;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Sparkle {
	float x;
	float y;
	private SpriteSheet image;
	private float scale;
	private PlayerSparkler daddy;
	private float increment;
	private float scale_max;
	private int startTime;
	private int time;
	
	public Sparkle(float x, float y, float max_scale, float incr, int start, String source, PlayerSparkler s) throws SlickException {
		daddy = s;
		time = 0;
		startTime = start;
		scale_max = max_scale;
		increment = incr;
		this.x = x;
		this.y = y;
		try{
        image = new SpriteSheet(source, 20, 20);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void draw(){
		if(x*y!=1&&time>startTime){
		image.draw(x-image.getWidth()/2*scale, y-image.getHeight()/2*scale);
		}
			
	}
	public void update(int delta){
		time+=delta;
		if(scale<scale_max&&time>=startTime){
			scale += (float)delta*increment;
		}
		else if(scale>=scale_max) renewPoint();
	}
	private void renewPoint(){
		time = 0;
		scale = 0.1f;
		float[] newPoint = daddy.getPoint();
		if(newPoint!=null){
		x = newPoint[0];
		y = newPoint[1];
		}
	}
}
