package com.shade.shadows;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.Grid;
import com.shade.crash.util.CrashGeom;
import com.shade.entities.Mushroom;
import com.shade.entities.Player;

/**
 * Builds on top of the CrashLevel to add support for shadows.
 * 
 * This assumes that all entities to be stored in the level are both
 * instances of the Body class and the ShadowCaster interface.
 * 
 * @author Alexander Schearer <aschearr@gmail.com>
 */
public class ShadowLevel implements Level {

    private static final float MAX_DISTANCE = 2500;
    private Grid grid;
    private Shadowscape shadowscape;
    private ZBuffer buffer;
    private LinkedList<Entity> in_queue, out_queue;
    
    public ShadowLevel(Grid grid) {
        this.grid = grid;
        buffer = new ZBuffer();
        out_queue = new LinkedList<Entity>();
        in_queue = new LinkedList<Entity>();
    }

    public void add(Entity e) {
        e.addToLevel(this);
        grid.add((Body) e);
        in_queue.add(e);
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

    public void render(Graphics g) {
        shadowscape.render(g);
        for (ShadowCaster e : buffer) {
            e.render(g);
        }
//        grid.debugDraw(g);
    }
    
    public void update(StateBasedGame game, int delta) {
        grid.update();
        for (ShadowCaster e : buffer) {
            e.update(game, delta);
        }
        resolve();
    }
    
    private void resolve() {
        for (Entity e : in_queue) {
            buffer.add((ShadowCaster) e);
        }
        for (Entity e : out_queue) {
            buffer.remove((ShadowCaster) e);
        }
        in_queue.clear();
        out_queue.clear();
    }
    
    /**
     * Update the shadowscape with a new light source.
     * @param direction
     */
    public void updateShadowscape(float direction) {
        resolve();
        shadowscape = new Shadowscape(buffer, direction, grid);
        // TODO this only makes sense if the shadowscape is updated every time
        for (ShadowCaster s : buffer) {
            if (s instanceof Mushroom) {
                ((Mushroom) s).shaded = shadowscape.contains((Mushroom) s);
            }
            if (s instanceof Player) {
                ((Player) s).shaded = shadowscape.contains((Player) s);
            }
        }
    }
    
    /**
     * Plant a mushroom randomly in the shadows.
     */
    public void plant() {
        try {
            Mushroom m = shadowscape.plant();
            add(m);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Return true if the body is in a shadow.
     * @param b
     * @return
     */
    public boolean shaded(Body b) {
        return shadowscape.contains(b);
    }
    
    /**
     * Return a list of mushrooms near this body.
     * @param b
     * @return
     */
    public Mushroom[] nearbyShrooms(Body b) {
        LinkedList<Mushroom> mushrooms = new LinkedList<Mushroom>();
        for (ShadowCaster s : buffer) {
            if (s instanceof Mushroom) {
                if (CrashGeom.distance2(b, (Body) s) < MAX_DISTANCE) {
                    mushrooms.add((Mushroom) s);
                }
            }
        }
        return mushrooms.toArray(new Mushroom[0]);
    }

    /**
     * Cast a ray from body one to body two return true if it reaches body two.
     * @param one
     * @param two
     * @return
     */
    public boolean ray(Body one, Body two) {
        return grid.ray(one, two);
    }

}
