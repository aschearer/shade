package com.shade.crash;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.crash.Grid;
import com.crash.Response;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.lighting.LuminousEntity;

/**
 * Concrete instance of the Level interface which has a grid underlying it for
 * collision detection.
 *
 * Note that all entities added to this level must extend the com.crash.Body
 * class or a class cast exception will occur.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class CrashLevel implements Level<LuminousEntity> {

    private Grid grid;
    private LinkedList<LuminousEntity> entities;

    public CrashLevel(int w, int h, int c) {
        entities = new LinkedList<LuminousEntity>();
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

    public void add(LuminousEntity e) {
        e.addToLevel(this);
        entities.add(e);
        grid.add((Body) e);
    }

    public void remove(LuminousEntity e) {
        e.removeFromLevel(this);
        entities.remove(e);
        grid.remove((Body) e);
    }

    public Object[] getEntitiesByRole(int role) {
        LinkedList<LuminousEntity> players = new LinkedList<LuminousEntity>();
        for (LuminousEntity e : entities) {
            if (e.getRole() == role) {
                players.add(e);
            }
        }
        return players.toArray();
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

    public LuminousEntity[] toArray(LuminousEntity[] a) {
        return entities.toArray(a);
    }

    public LuminousEntity[] toArray() {
        return entities.toArray(new LuminousEntity[0]);
    }

    public boolean lineOfSight(Entity one, Entity two, Body... exceptions) {
        return grid.ray((Body) one, (Body) two, exceptions);
    }

    public LuminousEntity[] nearbyEntities(final Entity subject, int threshold) {
        int threshold2 = threshold * threshold;
        LinkedList<LuminousEntity> neighbors = new LinkedList<LuminousEntity>();
        for (LuminousEntity e : entities) {
            if (CrashGeom.distance2((Body) subject, (Body) e) < threshold2) {
                neighbors.add(e);
            }
        }

        Collections.sort(neighbors, new Comparator<LuminousEntity>() {

            public int compare(LuminousEntity e1, LuminousEntity e2) {
                float d1 = CrashGeom.distance2((Body) subject, (Body) e1);
                float d2 = CrashGeom.distance2((Body) subject, (Body) e2);
                return (int) (d1 - d2);
            }

        });

        return neighbors.toArray(new LuminousEntity[0]);
    }
    

}
