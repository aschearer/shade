package com.shade.shadows;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.Grid;
import com.shade.entities.Mushroom;

/**
 * Builds on top of the CrashLevel to add support for shadows.
 * 
 * This assumes that all entities to be stored in the level are both
 * instances of the Body class and the ShadowCaster interface.
 * 
 * @author Alexander Schearer <aschearr@gmail.com>
 */
public class ShadowLevel implements Level {

    private Grid grid;
    private Shadowscape shadowscape;
    private ZBuffer buffer;
    private LinkedList<Entity> out_queue;
    
    public ShadowLevel(Grid grid) {
        this.grid = grid;
        buffer = new ZBuffer();
        out_queue = new LinkedList<Entity>();
    }

    public void add(Entity e) {
        e.addToLevel(this);
        grid.add((Body) e);
        buffer.add((ShadowCaster) e);
    }

    public void clear() {
        for (ShadowCaster s : buffer) {
            s.removeFromLevel(this);
        }
        grid.clear();
        buffer.clear();
    }

    public void remove(Entity e) {
        e.removeFromLevel(this);
        grid.remove((Body) e);
        out_queue.add(e);
    }
    
    public void plant() {
        try {
            Mushroom m = shadowscape.plant();
            add(m);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        shadowscape.render(g);
        for (ShadowCaster e : buffer) {
            e.render(g);
        }
    }
    
    public void updateShadowscape(float direction) {
        shadowscape = new Shadowscape(buffer, direction, grid);
        for (ShadowCaster s : buffer) {
            if (s instanceof Mushroom) {
                ((Mushroom) s).shaded = shadowscape.contains((Mushroom) s);
            }
        }
    }

    public void update(StateBasedGame game, int delta) {
        grid.update();
        for (ShadowCaster e : buffer) {
            e.update(game, delta);
        }
        resolve();
    }
    
    private void resolve() {
        for (Entity e : out_queue) {
            buffer.remove((ShadowCaster) e);
        }
        out_queue.clear();
    }

}
