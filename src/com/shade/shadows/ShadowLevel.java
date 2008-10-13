package com.shade.shadows;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.Grid;
import com.shade.crash.util.CrashGeom;

/**
 * Builds on top of the CrashLevel to add support for shadows.
 * 
 * This assumes that all entities to be stored in the level are both instances
 * of the Body class and the ShadowEntity interface. All ShadowCasters should be
 * placed into a Shadowscape instance prior to construction.
 * 
 * @author Alexander Schearer <aschearr@gmail.com>
 */
public class ShadowLevel implements Level {

    public static final float TRANSITION_TIME = 1f / 7;
    public static final float MAX_SHADOW = 0.6f;
    public static final float SUN_ANGLE_INCREMENT = 0.001f;
    public static final int SECONDS_PER_DAY = (int) Math.ceil(Math.PI * 32
            / SUN_ANGLE_INCREMENT);

    public enum DayLightStatus {
        DAWN, DAY, DUSK, NIGHT
    }

    private Grid grid;
    private Shadowscape shadowscape;
    private LinkedList<ShadowEntity> in_queue, out_queue;
    private ZBuffer entities;
    private float direction, rate;
    private DayLightStatus daylight;
    private int totalTime;

    public ShadowLevel(Grid grid, float direction, float depth, float rate) {
        this.grid = grid;
        this.direction = direction;
        this.rate = rate;
        daylight = DayLightStatus.DAY;
        shadowscape = new Shadowscape(daylight, direction, depth);
        entities = new ZBuffer();
        in_queue = new LinkedList<ShadowEntity>();
        out_queue = new LinkedList<ShadowEntity>();
    }

    public void add(Entity e) {
        e.addToLevel(this);
        in_queue.add((ShadowEntity) e);
        grid.add((Body) e);
    }
    
    public DayLightStatus getDayLight(){
    	return daylight;
    }

    /**
     * Override behavior for shadow casters so they get placed into the
     * shadowscape.
     * 
     * @param s
     */
    public void add(ShadowCaster s) {
        shadowscape.add(s);
        grid.add((Body) s);
    }

    public void clear() {
        shadowscape.clear();
        for (ShadowEntity e : entities) {
            e.removeFromLevel(this);
            out_queue.add(e);
        }
        grid.clear();
    }

    public void remove(Entity e) {
        e.removeFromLevel(this);
        out_queue.add((ShadowEntity) e);
        grid.remove((Body) e);
    }

    public void render(StateBasedGame game, Graphics g) {
    	//GL11.glDisable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (ShadowEntity e : entities) {
            e.render(game, g);
        }
        shadowscape.render(game, g);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        renderTimeOfDay(totalTime, game, g);
        //GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
    }

    private void renderTimeOfDay(int totaltime, StateBasedGame game, Graphics g) {
        int timeofday = totaltime % SECONDS_PER_DAY;
        // is it day or night?
        if (timeofday > 1.0 * SECONDS_PER_DAY * (1f / 2 - TRANSITION_TIME)) {
            daylight = DayLightStatus.NIGHT;
            float factor = MAX_SHADOW;
            float colorizer = 0;
            float colorizeg = 0;
            float colorizeb = 0;
            if (timeofday < 1.0 * SECONDS_PER_DAY / 2) {
                daylight = DayLightStatus.DUSK;
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
                daylight = DayLightStatus.DAWN;
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
        else daylight = DayLightStatus.DAY;

    }

    /**
     * Update the level as well as the shadowscape.
     * 
     * This should change the direction of the light as needed. It should also
     * update the shadow entities status as needed.
     */
    public void update(StateBasedGame game, int delta) {
        totalTime += delta;
        resolve();
        grid.update();
        direction += rate;
        shadowscape.setDirection(direction);
        shadowscape.setDayLight(daylight);
        shadowscape.update(game, delta);
        for (ShadowEntity e : entities) {
            e.update(game, delta);
        }
        updateIntensities();
    }

    private void updateIntensities() {
        for (ShadowEntity e : entities) {
            e.setIntensity(shadowscape.contains((Body) e));
        }
    }

    private void resolve() {
        for (ShadowEntity e : in_queue) {
            entities.add(e);
        }
        for (ShadowEntity e : out_queue) {
            entities.remove(e);
        }
        in_queue.clear();
        out_queue.clear();
    }

    /**
     * Returns a random point in the shadowscape such that it is not
     * intersecting with anything.
     */
    public Vector2f randomPoint(GameContainer container) {
        Vector2f p = shadowscape.randomShadowedPoint();
        while (!validPoint(p, container)) {
            p = shadowscape.randomShadowedPoint();
        }
        return p;
    }

    private boolean validPoint(Vector2f p, GameContainer container) {
        if (!(p.x > 0 && p.x < container.getWidth())) {
            return false;
        }
        if (!(p.y > 0 && p.y < container.getHeight())) {
            return false;
        }
        if (!grid.hasRoom(p, 48)) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if one can "see" two. This means that there is a clear path
     * between the two objects. This is useful when AI needs to determine
     * whether to pursue a target.
     * 
     * @param one
     * @param two
     * @return
     */
    public boolean lineOfSight(Entity one, Entity two, Body ... exceptions) {
        return grid.ray((Body) one, (Body) two, exceptions);
    }

    /**
     * Return a list of entities within a given distance to the subject.
     * 
     * This is useful for AI which needs to hunt down nearby targets. It allows
     * the AI to focus in on neighbors, for instance it may check whether it has
     * a direct line of sight with its neigbhors.
     * 
     * @param subject
     * @param threshold
     * @return
     */
    public ShadowEntity[] nearByEntities(final Entity subject, int threshold) {
        int threshold2 = threshold * threshold;
        LinkedList<ShadowEntity> neighbors = new LinkedList<ShadowEntity>();
        for (ShadowEntity e : entities) {
            if (CrashGeom.distance2((Body) subject, (Body) e) < threshold2) {
                neighbors.add(e);
            }
        }
        // TODO verify this is sorting in the right order
        Collections.sort(neighbors, new Comparator<ShadowEntity>() {

            public int compare(ShadowEntity e1, ShadowEntity e2) {
                float d1 = CrashGeom.distance2((Body) subject, (Body) e1);
                float d2 = CrashGeom.distance2((Body) subject, (Body) e2);
                return (int) (d1 - d2);
            }

        });

        return neighbors.toArray(new ShadowEntity[0]);
    }

}
