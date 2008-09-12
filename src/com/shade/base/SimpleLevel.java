package com.shade.base;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public class SimpleLevel implements Level {
    
    private LinkedList<Entity> entities;
    
    public SimpleLevel() {
        entities = new LinkedList<Entity>();
    }

    public void add(Entity e) {
        e.addToLevel(this);
        entities.add(e);
    }

    public void clear() {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).removeFromLevel(this);
            entities.remove(i);
        }
    }

    public void remove(Entity e) {
        e.removeFromLevel(this);
        entities.remove(e);
    }

    public void render(Graphics g) {
        for (Entity e : entities) {
            e.render(g);
        }
    }

    public void update(StateBasedGame game, int delta) {
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update(game, delta);
        }
    }

}
