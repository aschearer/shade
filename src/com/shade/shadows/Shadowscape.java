package com.shade.shadows;

import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.crash.Body;
import com.shade.shadows.ShadowEntity.ShadowIntensity;
import com.shade.shadows.ShadowLevel.DayLightStatus;

/**
 * A collection of shadow casters. Their shadows are combined to construct the
 * shadowscape.
 * 
 * Generally, the player or mushroom are OK when contained by the shadowscape
 * and are in danger when not.
 * 
 * The shadow casters and shadowscape can be rendered in separate passes for
 * convenience.
 * 
 * This class is implemented under the assumption that its bodies don't need
 * access to the level, that they are just dumb obstacles which are not added or
 * removed dynamically.
 * 
 * @author Alexander Schearer<aschearer@gmail.com>
 */
class Shadowscape implements Animatable {

    private static final Color SHADOW_COLOR = new Color(0, 0, 0, .5f);
    private LinkedList<ShadowCaster> casters;
    private LinkedList<Shape> shadows;

    /**
     * The direction and height of the light source.
     */
    private float direction, depth;
    private boolean updated;
    private DayLightStatus daylight;
    
    public Shadowscape(DayLightStatus daylight, float direction, float depth) {
        setDayLight(daylight);
        setDirection(direction);
        setDepth(depth);
        casters = new LinkedList<ShadowCaster>();
        shadows = new LinkedList<Shape>();
    }

    public void setDirection(float d) {
        direction = d;
        updated = true;
    }

    public void setDepth(float d) {
        depth = d;
        updated = true;
    }

    public void setDayLight(DayLightStatus d) {
        daylight = d;
    }

    public void add(ShadowCaster s) {
        casters.add(s);
    }

    public void renderShadowscape(StateBasedGame game, Graphics g) {
        Color c = g.getColor();
        g.setColor(SHADOW_COLOR);
        g.setAntiAlias(true);
        for (Shape s : shadows) {
            g.fill(s);
        }
        g.setAntiAlias(false);
        g.setColor(c);
    }

    public void renderShadowCasters(StateBasedGame game, Graphics g) {
        for (ShadowCaster s : casters) {
            s.render(game, g);
        }
    }

    /**
     * Render the shadowscape followed by the shadow casters.
     * 
     * This is just implemented for completeness. Most likely you will want to
     * render the shadowscape, shadow entities, shadow casters.
     */
    public void render(StateBasedGame game, Graphics g) {
        renderShadowscape(game, g);
        renderShadowCasters(game, g);
    }

    /**
     * Calls update on each of the shadow casters and if the direction or depth
     * has changed then update the shadowscape.
     */
    public void update(StateBasedGame game, int delta) {
        for (ShadowCaster s : casters) {
            s.update(game, delta);
        }
        testAndUpdate();
    }

    private void testAndUpdate() {
        if (updated) {
            updated = false;
            shadows.clear();
            for (ShadowCaster s : casters) {
                shadows.add(s.castShadow(direction, depth));
            }
        }
    }

    public void clear() {
        shadows.clear();
        casters.clear();
    }

    /**
     * Return a point which is contained by the shadowscape.
     * 
     * This is used to place mushrooms among other things. This does not check
     * whether the space is occupied. Check for that in ShadeLevel.
     * 
     * @return
     */
    public Vector2f randomShadowedPoint() {
        Shape shadow = getRandomShadow();
        int tries = 0;
        boolean finished = false;
        Vector2f p = null;
        while (!finished) {
            if (tries == 3) {
                tries = 0;
                shadow = getRandomShadow();
            }
            tries++;
            p = getRandomPointIn(shadow);
            finished = checkPoint(p, shadow);
        }
        return p;
    }

    private Shape getRandomShadow() {
        int i = (int) (shadows.size() * Math.random());
        return shadows.get(i);
    }

    private Vector2f getRandomPointIn(Shape shadow) {
        float minX = shadow.getMinX();
        float minY = shadow.getMinY();
        float maxX = shadow.getMaxX();
        float maxY = shadow.getMaxY();

        float x = (float) ((maxX - minX) * Math.random()) + minX;
        float y = (float) ((maxY - minY) * Math.random()) + minY;

        return new Vector2f(x, y);
    }

    private boolean checkPoint(Vector2f p, Shape shadow) {
        // p is in the shadow
        if (!shadow.contains(p.x, p.y)) {
            return false;
        }
        // ok we're done probing
        return true;
    }

    public ShadowIntensity contains(Body e) {
        for (Shape s : shadows) {
            if (s.contains(e.getCenterX(), e.getCenterY())) {
                return ShadowIntensity.CASTSHADOWED;
            }
        }
        if (daylight == DayLightStatus.NIGHT) {
            return ShadowIntensity.SHADOWED;
        }
        return ShadowIntensity.UNSHADOWED;
    }
}
