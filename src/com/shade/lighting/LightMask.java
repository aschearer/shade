package com.shade.lighting;

import java.util.Arrays;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import org.newdawn.slick.state.StateBasedGame;

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


    protected final static Color SHADE = new Color(0, 0, 0, .3f);

    private int threshold;
    private LinkedList<LightSource> lights;

    public LightMask(int threshold) {
        this.threshold = threshold;
        lights = new LinkedList<LightSource>();
    }

    public void add(LightSource light) {
        lights.add(light);
    }

    public void render(StateBasedGame game, Graphics g,
                       LuminousEntity[] entities, Image... backgrounds) {
        renderLights(game, g, entities);
        renderBackgrounds(game, g, backgrounds);
        renderEntities(game, g, entities);
    }

    private void renderLights(StateBasedGame game, Graphics g,
                              LuminousEntity... entities) {
        for (LightSource light : lights) {
            light.render(game, g, entities);
        }

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

        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
        while (i < entities.length && entities[i].getZIndex() < threshold) {
            entities[i].render(game, g);
            entities[i].setLuminosity(getLuminosityFor(entities[i], g));
            i++;
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        while (i < entities.length) {
            entities[i].render(game, g);
            entities[i].setLuminosity(getLuminosityFor(entities[i], g));
            i++;
        }
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
        GL11.glClearStencil(0);
        // write a one to the stencil buffer everywhere we are about to draw
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        // this is to always pass a one to the stencil buffer where we draw
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
    }

    /**
     * Called after drawing the shadows cast by a light.
     */
    protected static void disableStencil() {
        // resume drawing to everything
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1);

        // don't modify the contents of the stencil buffer
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
    }

}
