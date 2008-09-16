package com.shade.shadows;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.Grid;

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
    
    public ShadowLevel(Grid grid) {
        this.grid = grid;
        buffer = new ZBuffer();
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
        buffer.remove((ShadowCaster) e);
    }

    public void render(Graphics g) {
        shadowscape.render(g);
        for (ShadowCaster e : buffer) {
            e.render(g);
        }
    }
    
    public void updateShadowscape(float direction) {
        shadowscape = new Shadowscape(buffer, direction);
    }

    public void update(StateBasedGame game, int delta) {
        grid.update();
        for (int i = 0; i < buffer.size(); i++) {
            buffer.get(i).update(game, delta);
        }
    }

}
