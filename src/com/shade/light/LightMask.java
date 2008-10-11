package com.shade.light;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.*;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.shadows.ShadowCaster;

public class LightMask {
	ImageBuffer lightBuffer;
	Image i;
	float depth;
	boolean state=false;
	
	private ArrayList<ShadowCaster> shadowcasters;
	private ArrayList<LightSource> lights;
	private ArrayList<Shape> shadows;
	
	public LightMask(int width, int height){
		depth = 10;
		shadowcasters = new ArrayList<ShadowCaster>();
		lights = new ArrayList<LightSource>();
		shadows = new ArrayList<Shape>();
		
		lightBuffer = new ImageBuffer(width, height);
		for(int i=0;i<lightBuffer.getWidth();i++){
			for(int j=0;j<lightBuffer.getHeight();j++){
				lightBuffer.setRGBA(i, j, 0, 0, 0, 100);
			}
		}
		i = lightBuffer.getImage();
	}
	
	public void update(StateBasedGame game, int delta, ArrayList<ShadowCaster> shadowcs, ArrayList<LightSource> light){
		shadowcasters = shadowcs;
		lights = light;
		for(LightSource l:lights)
			l.update(game, delta);
	}
	
	
	public void add(ShadowCaster c){
		shadowcasters.add(c);
	}
	
	public void add(LightSource l){
		lights.add(l);
	}
	
	public void update(StateBasedGame game, int delta){
		this.update(game, delta, shadowcasters, lights);
	}
	
	public float intensityAt(int x, int y){
		return lightBuffer.getImage().getColor(x, y).a;
	}
	public void renderLightsyuck(Graphics g){
		for(LightSource l: lights){
			GL11.glDisable(GL11.GL_BLEND);
			Vector2f pos = l.getPosition();
			float i = l.getIntensity();
			g.setColor(new Color(0,0,0,0));
			for(ShadowCaster shad: shadowcasters){
				Vector2f objectpos = shad.getPosition();
				double diffy = objectpos.y-pos.y;
				double diffx = objectpos.x-pos.x;
				double angle = Math.atan2(diffy,diffx);
				Shape s = shad.castShadow((float)(angle+Math.PI/2), depth);
				g.fill(s);
			}
			g.setColor(new Color(1.0f,1.0f,0f,1.0f));
			g.drawOval(pos.x, pos.y, 5, 5);
			g.setColor(new Color(0,0,0,i));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_DST_ALPHA,GL11.GL_ONE);
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
			
			g.fill(new Rectangle(0, 0, lightBuffer.getWidth(), lightBuffer.getHeight()));
			GL11.glDisable(GL11.GL_BLEND);
			
		}
	}
	

	
	public void render(Graphics g){
		if(!state){
			SlickCallable.enterSafeBlock();
			try{
			Display.create(new PixelFormat(8,1,4));
			Display.makeCurrent();
			Display.destroy();
			} catch (Exception e)
			{
				System.out.println("Your hardware failed! WHEE");
				System.out.println(e);
			}
			SlickCallable.leaveSafeBlock();
			state = true;
		}
		// render in full light
		  /*
		  GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT |
		             GL11.GL_DEPTH_BUFFER_BIT |
		             GL11.GL_STENCIL_BUFFER_BIT);
		  //*/
		g.setColor(new Color(0,0,0,0.6f));
		g.fill(new Rectangle(0,0,800,600));
		renderLights(g);
		g.setColor(Color.white);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
		

		//g.setDrawMode(g.MODE_ALPHA_BLEND);


		//g.setDrawMode(Graphics.MODE_NORMAL);
	}
	
//=============OLD VERSION OF THE RENDERLIGHTS METHOD============
	public void renderLights(Graphics g){
		//make everything kinda visible
		///*
		for(LightSource l: lights){
			//write only to the stencil buffer
			///*
			GL11.glColorMask(false, false, false, false);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			System.out.println("Stencil enabled? "+GL11.glIsEnabled(GL11.GL_STENCIL_TEST));
			GL11.glClearStencil(1);
			// write a one to the stencil buffer everywhere we are about to draw
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
			
			// this is to always pass a one to the stencil buffer where we draw
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
			//*/
			Vector2f pos = l.getPosition();
			///*
			FloatBuffer buff = BufferUtils.createFloatBuffer(1);
			GL11.glReadPixels((int)pos.x,(int)pos.y, 1,1, GL11.GL_STENCIL_INDEX, GL11.GL_FLOAT, buff);
			float f = buff.get(0);
			System.out.println("Light value: "+f);
			//*/
			float i = l.getIntensity();
			g.setColor(new Color(0,0,0,0));
			for(ShadowCaster shad: shadowcasters){
				Vector2f objectpos = shad.getPosition();
				double diffy = objectpos.y-pos.y;
				double diffx = objectpos.x-pos.x;
				double angle = Math.atan2(diffy,diffx);
				Shape s = shad.castShadow((float)(angle+Math.PI/2), depth);
				g.fill(s);
				/*
				FloatBuffer buffs = BufferUtils.createFloatBuffer(16);
				GL11.glReadPixels((int)objectpos.x,(int)objectpos.y, 4,4, GL11.GL_STENCIL_INDEX, GL11.GL_FLOAT, buffs);
				float fs = buffs.get(0);
				System.out.println("ShadowValue value: "+fs);
				//*/
			}
			
			//resume drawing to everything
			GL11.glDepthMask(true);
			GL11.glColorMask(true, true, true, true);
			
			g.setColor(new Color(1.0f,1.0f,0f,1.0f));
			g.drawOval(pos.x, pos.y, 5, 5);
			GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1);
			//GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
			//GL11.glStencilFunc(GL11.GL_NEVER, 1, 1);


			// don't modify the contents of the stencil buffer
			//GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			g.setColor(new Color(0,0,0,i));
			GL11.glEnable(GL11.GL_BLEND);
			//GL11.glBlendFunc(GL11.GL_DST_ALPHA,GL11.GL_ONE);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
			
			//g.fill(new Rectangle(0, 0, lightBuffer.getWidth(), lightBuffer.getHeight()));
			GL11.glDisable(GL11.GL_BLEND);
			//GL11.glDisable(GL11.GL_STENCIL_TEST);
			//System.out.println("Stencil enabled? "+GL11.glIsEnabled(GL11.GL_STENCIL_TEST));
			
			
		}
		//*/
	}
}
