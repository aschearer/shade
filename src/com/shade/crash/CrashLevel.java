package com.shade.crash;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

/**
 * Level which assumes that its entities are instances of the Body class.
 * @author Alex Schearer <aschearer@gmail.com>
 */
public class CrashLevel implements Level {
    
    private Grid grid;
    private LinkedList<Entity> entities;
    
    public CrashLevel(Grid grid) {
        this.grid = grid;
        entities = new LinkedList<Entity>();
    }

    public void add(Entity e) {
        e.addToLevel(this);
        grid.add((Body) e);
        entities.add(e);
    }

    public void clear() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).removeFromLevel(this);
            grid.remove((Body) entities.get(i));
            entities.remove(i);
        }
    }

    public void remove(Entity e) {
        e.removeFromLevel(this);
        grid.remove((Body) e);
        entities.remove(e);
    }

    public void render(Graphics g) {
        for (Entity e : entities) {
            e.render(g);
        }
    }

    public void update(StateBasedGame game, int delta) {
        grid.update();
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update(game, delta);
        }
    }

}
