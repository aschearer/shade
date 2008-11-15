package com.shade.lighting;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

public class GlobalLight implements LightSource {

    private float angle, depth;

    public GlobalLight(float depth, float angle) {
        this.depth = depth;
        this.angle = angle;
    }

    public void render(StateBasedGame game, Graphics g,
            LuminousEntity... entities) {
        LightMask.enableStencil();
        for (LuminousEntity entity : entities) {
            Shape s = entity.castShadow(angle, depth);
            if (s != null) {
                g.fill(s);
            }
        }
        LightMask.disableStencil();

        GameContainer c = game.getContainer();
        g.setColor(Color.black);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g.setColor(Color.white);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void update(StateBasedGame game, int delta) {
        angle += .0005f;
    }

}
