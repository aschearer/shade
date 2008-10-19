package com.shade.lightrender;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.shadows.ShadowCaster;
import com.shade.shadows.ShadowEntity;
import com.shade.shadows.ShadowLevel.DayLightStatus;

public class LightMask{
	

	private int width;
	private int height;
	private ArrayList<ShadowCaster> shadowcasters;
	private ArrayList<ShadowEntity> shadowentities;
	private ArrayList<LightSource> lights;
	private ArrayList<Image> backgrounds;
	
	public LightMask(int w, int h){
		shadowcasters = new ArrayList<ShadowCaster>();
		lights = new ArrayList<LightSource>();
		shadowentities = new ArrayList<ShadowEntity>();
		backgrounds = new ArrayList<Image>();
		width = w;
		height = h;
	}
	

	
	public void add(ShadowEntity e){
		shadowentities.add(e);
	}
	
	public void add(ShadowCaster c){
		shadowcasters.add(c);
	}
	
	public void add(LightSource l){
		lights.add(l);
	}
	
	public void add(Image i){
		backgrounds.add(i);
	}
	
	public void update(StateBasedGame game, int delta){
		for(LightSource l:lights)
			l.update(game, delta);
	}
	

	
	public void render(StateBasedGame game, Graphics g){
		renderLights(g);
		//renderBackgrounds(g);
		renderEntities(game, g);
		renderCasters(game, g);
	}
	
	public void renderBackgrounds(Graphics g){
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for(Image i:backgrounds){
			i.draw();
		}
	}
	
	public void renderEntities(StateBasedGame game, Graphics g){
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
		for(ShadowEntity e:shadowentities)
			e.render(game, g);
	}
	
	public void renderCasters(StateBasedGame game, Graphics g){
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for(ShadowCaster c:shadowcasters)
			c.render(game, g);
	}
	
	public void renderLights(Graphics g){
		
		for(int index = 0;index<lights.size();index++){
			LightSource l = lights.get(index);
			
			//write only to the stencil buffer
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glColorMask(false, false, false, false);
			GL11.glDepthMask(false);
			GL11.glClearStencil(0);
			// write a one to the stencil buffer everywhere we are about to draw
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);		
			// this is to always pass a one to the stencil buffer where we draw
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
			
			
			Vector2f pos = l.getPosition();
			g.setColor(new Color(0,0,0,1.0f));
			for(ShadowCaster shad: shadowcasters){
				Vector2f objectpos = shad.getPosition();
				double diffy = objectpos.y-pos.y;
				double diffx = objectpos.x-pos.x;
				double angle = Math.atan2(diffy,diffx);
				Shape s = shad.castShadow((float)(angle+Math.PI/2), l.getCastLength());
				g.fill(s);
			}
			
			//resume drawing to everything
			GL11.glDepthMask(true);
			GL11.glColorMask(true, true, true, true);
			GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1);


			// don't modify the contents of the stencil buffer
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			l.renderLight(g, width, height);
			
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

			
			
			
		}
		drawLights(g);
	}
	
	private void drawLights(Graphics g){
		for(LightSource l: lights){
			Vector2f pos = l.getPosition();
			g.setColor(new Color(1.0f,1.0f,0f,1.0f));
			g.drawOval(pos.x, pos.y, 5, 5);
		}
		
	}
	
	private class LightSorter implements Comparator<LightSource>{

		
		public int compare(LightSource arg0, LightSource arg1) {
			double diff = arg0.getIntensity()-arg1.getIntensity();
			return (int)(diff/Math.abs(diff));
		}
	
	}

}
