package com.shade.shadows;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.Grid;
import com.shade.crash.util.CrashGeom;
import com.shade.entities.Mushroom;
import com.shade.entities.Player;
import com.shade.shadows.ShadowCaster.DaylightStatus;

/**
 * Builds on top of the CrashLevel to add support for shadows.
 * 
 * This assumes that all entities to be stored in the level are both instances
 * of the Body class and the ShadowCaster interface.
 * 
 * @author Alexander Schearer <aschearr@gmail.com>
 */
public class ShadowLevel implements Level {

    public static final float TRANSITION_TIME = 1f / 7;
    public static final float MAX_SHADOW = 0.6f;
    public static final float SUN_ANGLE_INCREMENT = 0.001f;
    public static final int SECONDS_PER_DAY = (int) Math.ceil(Math.PI * 32
            / SUN_ANGLE_INCREMENT);

    private static final float MAX_DISTANCE = 40000;

    public enum ShadowStatus {
        UNSHADOWED, SHADOWED, CASTSHADOWED
    };

    private DaylightStatus daylight;
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

    public boolean isNight() {
        return daylight == DaylightStatus.NIGHT;
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

    public void render(StateBasedGame game, Graphics g) {
        for (ShadowCaster e : buffer.under(5)) {
            e.render(game, g);
        }
        shadowscape.render(g);
        for (ShadowCaster e : buffer.over(5)) {
            e.render(game, g);
        }
        // grid.debugDraw(g);
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

    public void renderTimeOfDay(int totaltime, StateBasedGame game, Graphics g) {
        int timeofday = totaltime % SECONDS_PER_DAY;
        // is it day or night?
        if (timeofday > 1.0 * SECONDS_PER_DAY * (1f / 2 - TRANSITION_TIME)) {
            daylight = DaylightStatus.NIGHT;
            float factor = MAX_SHADOW;
            float colorizer = 0;
            float colorizeg = 0;
            float colorizeb = 0;
            if (timeofday < 1.0 * SECONDS_PER_DAY / 2) {
                daylight = DaylightStatus.DAWN;
                factor = (float) 1.0
                        * MAX_SHADOW
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1);
                colorizer = 0.2f * (float) Math.abs(Math.sin(Math.PI
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
                colorizeg = 0.1f * (float) Math.abs(Math.sin(Math.PI
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));

            }
            if (timeofday > 1.0 * SECONDS_PER_DAY * (1 - TRANSITION_TIME)) {
                daylight = DaylightStatus.DUSK;
                factor = MAX_SHADOW * (SECONDS_PER_DAY - timeofday)
                        / (SECONDS_PER_DAY * TRANSITION_TIME);
                colorizer = 0.1f * (float) Math.abs(Math.cos(Math.PI
                        / 2
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
                colorizeg = 0.1f * (float) Math.abs(Math.cos(Math.PI
                        / 2
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
                colorizeb = 0.05f * (float) Math.abs(Math.cos(Math.PI
                        / 2
                        * ((timeofday - SECONDS_PER_DAY / 2f)
                                / (SECONDS_PER_DAY * TRANSITION_TIME) + 1)));
            }
            Color night = new Color(colorizer, colorizeg, colorizeb, factor);

            g.setColor(night);
            g.fillRect(0, 0, game.getContainer().getScreenWidth(), game
                    .getContainer().getScreenHeight());
            g.setColor(Color.white);
        }

    }

    /**
     * Update the shadowscape with a new light source.
     * 
     * @param direction
     */
    public void updateShadowscape(float direction, float shadowLength) {
        resolve();
        shadowscape = new Shadowscape(buffer, direction, shadowLength, grid,
                daylight);
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
     * 
     * @param b
     * @return
     */
    public boolean shaded(Body b) {
        return shadowscape.contains(b) == ShadowStatus.CASTSHADOWED;
    }

    /**
     * Return a list of mushrooms near this body.
     * 
     * @param b
     * @return
     */
    public Mushroom[] nearbyShrooms(final Body b) {
        LinkedList<Mushroom> mushrooms = new LinkedList<Mushroom>();
        for (ShadowCaster s : buffer) {
            if (s instanceof Mushroom) {
                if (CrashGeom.distance2(b, (Body) s) < MAX_DISTANCE) {
                    mushrooms.add((Mushroom) s);
                }
            }
        }

        Collections.sort(mushrooms, new Comparator<Mushroom>() {

            public int compare(Mushroom m1, Mushroom m2) {
                return (int) (CrashGeom.distance2(b, m1) - CrashGeom
                        .distance2(b, m2));
            }

        });

        return mushrooms.toArray(new Mushroom[0]);
    }

    /**
     * Cast a ray from body one to body two return true if it reaches body two.
     * 
     * @param one
     * @param two
     * @return
     */
    public boolean ray(Body one, Body two) {
        return grid.ray(one, two);
    }

    /**
     * Determine if there is a certain amount of free space around a given
     * point.
     * 
     * @param x
     * @param y
     * @param r
     * @return
     */
    public boolean clear(float x, float y, float r) {
        return grid.hasRoom(new Vector2f(x, y), r * 2);
    }

}
