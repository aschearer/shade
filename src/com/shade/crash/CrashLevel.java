package com.shade.crash;

import java.util.LinkedList;

import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.crash.Grid;
import com.crash.Response;
import com.shade.base.Entity;
import com.shade.base.Level;

/**
 * Concrete instance of the Level interface which has a grid underlying it for
 * collision detection.
 * 
 * Note that all entities added to this level must extend the com.crash.Body
 * class or a class cast exception will occur.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class CrashLevel<T extends Entity> implements Level<T> {

	private Grid grid;
	private LinkedList<T> entities;

	public CrashLevel(int w, int h, int c) {
		entities = new LinkedList<T>();
		grid = new Grid(w, h, c);
		grid.setResponse(new Response() {

			public void respond(Body one, Body two) {
				Entity e1 = (Entity) one;
				Entity e2 = (Entity) two;

				e1.onCollision(e2);
				e2.onCollision(e1);
			}

		});
	}

	public void add(T e) {
		e.addToLevel(this);
		entities.add(e);
		grid.add((Body) e);
	}

	public void remove(T e) {
		e.removeFromLevel(this);
		entities.remove(e);
		grid.remove((Body) e);
	}

	public void clear() {
		for (Entity e : entities) {
			e.removeFromLevel(this);
		}
		entities.clear();
		grid.clear();
	}

	public void update(StateBasedGame game, int delta) {
		grid.update();
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update(game, delta);
		}
	}
	
	public T[] toArray(T[] a) {
		return entities.toArray(a);
	}

}
