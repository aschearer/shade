package com.shade.lighting;

import java.util.Arrays;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.DayPhaseTimer;
import com.shade.entities.Roles;

/**
 * A view which renders a set of entities, lights, and background images in such
 * a way as to generate dynamic lighting.
 *
 * It's safe to draw things to the screen after calling LightMask.render if you
 * want them to appear above the gameplay, for instance user controls.
 *
 * <em>Note that calling render will update your entities' luminosity. Please
 * direct any hate mail to JJ Jou.</em>
 *
 * @author JJ Jou <j.j@duke.edu>
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LightMask {

    /* Set to remove white borders from player, mushrooms, etc. */
    private static final float MAGIC_ALPHA_VALUE = .65f;
    private static final float MAGIC_ARROW_VALUE = .1f;
    protected final static Color SHADE = new Color(0, 0, 0, .3f);
    public static final float MAX_DARKNESS = 0.4f;
    private DayPhaseTimer timer;
    
    /**======================END CONSTANTS=======================*/

    private int threshold;
    private LinkedList<LightSource> lights;

    public LightMask(int threshold, DayPhaseTimer time) {
        this.threshold = threshold;
        lights = new LinkedList<LightSource>();
        timer = time;
    }

    public void add(LightSource light) {
        lights.add(light);
    }

    public void render(StateBasedGame game, Graphics g,
                       LuminousEntity[] entities, Image... backgrounds) {
        renderLights(game, g, entities);
        renderBackgrounds(game, g, backgrounds);
        renderEntities(game, g, entities);
        //RENDER NIGHT! WHEEE
        renderTimeOfDay(game, g);
    }
    
    public void renderTimeOfDay(StateBasedGame game, Graphics g){
    	Color c = g.getColor();
    	if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.DUSK){
    		g.setColor(new Color(1-timer.timeLeft(),1-timer.timeLeft(),0f,MAX_DARKNESS*timer.timeLeft()));
    		g.fillRect(0, 0, game.getContainer().getWidth(), game.getContainer().getHeight());
    	}
    	else if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.NIGHT){
    		g.setColor(new Color(0,0,0,MAX_DARKNESS));
    		g.fillRect(0, 0, game.getContainer().getWidth(), game.getContainer().getHeight());
    	}
    	else if(timer.getDaylightStatus()==DayPhaseTimer.DayLightStatus.DAWN){
    		g.setColor(new Color(timer.timeLeft(),timer.timeLeft(),0,MAX_DARKNESS*(1-timer.timeLeft())));
    		g.fillRect(0, 0, game.getContainer().getWidth(), game.getContainer().getHeight());
    	}
    	g.setColor(c);
    
    }
    

    private void renderLights(StateBasedGame game, Graphics g,
                              LuminousEntity... entities) {
    	enableStencil();
        for (LightSource light : lights) {
            light.render(game, g, entities);
        }
        disableStencil();

        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        g.setColor(SHADE);
        GameContainer c = game.getContainer();
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);
    }

    private void renderBackgrounds(StateBasedGame game, Graphics g,
                                   Image... backgrounds) {
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
        for (Image background : backgrounds) {
            background.draw();
        }
    }

    private void renderEntities(StateBasedGame game, Graphics g,
                                LuminousEntity... entities) {
        Arrays.sort(entities);
        int i = 0;
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.2f);
        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        while (i < entities.length && entities[i].getZIndex() < threshold) {
            if (entityException(entities[i])) {
                GL11.glAlphaFunc(GL11.GL_GREATER, MAGIC_ARROW_VALUE);
            } else {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.95f);
            }
            entities[i].setLuminosity(getLuminosityFor(entities[i], g));
            entities[i].render(game, g);
            i++;
        }

        GL11.glAlphaFunc(GL11.GL_GREATER, 0f);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        while (i < entities.length) {
        	entities[i].setLuminosity(getLuminosityFor(entities[i], g));
            entities[i].render(game, g);
            i++;
        }
        //GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
    
    private boolean entityException(LuminousEntity e) {
        return (e.getRole() == Roles.DUMMY.ordinal());
    }

    private float getLuminosityFor(LuminousEntity entity, Graphics g) {
        return g.getPixel((int) entity.getXCenter(), (int) entity.getYCenter()).a;
    }

    /**
     * Called before drawing the shadows cast by a light.
     */
    protected static void enableStencil() {
        // write only to the stencil buffer
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
    }
    
    protected static void resetStencil(){
    	GL11.glClearStencil(0);
        // write a one to the stencil buffer everywhere we are about to draw
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        // this is to always pass a one to the stencil buffer where we draw
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
    }
    
    /**
     * Called after drawing the shadows cast by a light.
     */
    protected static void keepStencil() {
        // resume drawing to everything
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1);

        // don't modify the contents of the stencil buffer
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
    }
    
    protected static void disableStencil(){
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    }

}
