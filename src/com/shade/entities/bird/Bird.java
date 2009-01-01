package com.shade.entities.bird;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.StateManager;
import com.shade.controls.DayPhaseTimer;
import com.shade.crash.CrashLevel;
import com.shade.crash.Repelable;
import com.shade.entities.Basket;
import com.shade.entities.Player;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.levels.Model;
import com.shade.lighting.LuminousEntity;

/**
 * The real deal; this mole is the sum of different mole states.
 * 
 * No I haven't heard of encapsulation.
 * 
 * TODO: Make the birds edge-of-level smart - CUT. TODO: Make the bird flight
 * more realistic by adding some phases TODO: Make the bird get "mad" as the
 * player approaches TODO: Make the bird "land" correctly on fences - done.
 * 
 * @author Jonathan Jou <j.j@duke.edu>
 */
public final class Bird extends Body implements LuminousEntity {

    protected enum States {
        WAITING, RETURNING, ATTACKING, SLEEPING, MIGRATING
    }

    protected CrashLevel level;
    protected StateManager manager;
    protected Mushroom target;
    protected float heading;
    protected float range;
    protected float speed;
    protected boolean attacking;

    private float luminosity;

    protected static Sound alert, attack;

    static {
        try {
            alert = new Sound("entities/bird/alert.ogg");
            attack = new Sound("entities/bird/attack.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Bird(int x, int y, int range, float speed) throws SlickException {
        heading = (float) (Math.PI);
        this.range = range;
        this.speed = speed;
        initShape(x, y);
        initStates();
    }

    public boolean isAttacking() {
        return attacking;
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 21f);
    }

    private void initStates() throws SlickException {
        manager = new StateManager();
        manager.add(new ReturningBird(this));
        manager.add(new AttackingBird(this));
        manager.add(new WaitingBird(this));
        manager.add(new SleepingBird(this));

    }

    protected void kill() {
        level.remove(this);
    }

    public void move(double rate) {
        float x = (float) (Math.cos(heading - Math.PI / 2) * speed * rate);
        float y = (float) (Math.sin(heading - Math.PI / 2) * speed * rate);
        nudge(x, y);
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level<?> l) {
        level = (CrashLevel) l;
    }

    public int getRole() {
        return Roles.BIRD.ordinal();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
    }

    public boolean canChase() {
        return playerInSight() && playerInRange();
    }

    public boolean playerInSight() {
        Object[] o = level.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (o.length > 0) {
            Player p = (Player) o[0];
            return level.lineOfSight(this, p, this, (Basket) level
                    .getEntitiesByRole(Roles.BASKET.ordinal())[0])
                    && p.getLuminosity() > 0.6;
        }
        return false;
    }

    public boolean playerInRange() {
        Object[] o = level.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (o.length > 0) {
            Player p = (Player) o[0];
            float distx = p.getXCenter() - getXCenter();
            float disty = p.getYCenter() - getYCenter();
            return Math.sqrt(distx * distx + disty * disty) < range;
        }
        return false;
    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getXCenter(), getYCenter(), (float) Math.toDegrees(heading));
        manager.render(game, g);
        g.resetTransform();
    }

    public void yawn() {
        Model mode = (Model) level;
        if (mode.getTimer().getDaylightStatus() == DayPhaseTimer.DayLightStatus.NIGHT) {
            manager.enter(States.SLEEPING);
        }
    }

    public void wake() {
        Model mode = (Model) level;
        if (mode.getTimer().getDaylightStatus() == DayPhaseTimer.DayLightStatus.DAWN) {
            manager.enter(States.WAITING);
        }
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();

    }

    /**
     * Checks whether a linkable is over the edge of the screen and wraps it if
     * it is.
     */
    protected void testAndWrap() {
        if (getXCenter() <= 5) {
            shape.setCenterX(795);
        }
        if (getXCenter() > 795) {
            shape.setCenterX(5);
        }
        if (getYCenter() <= 5) {
            shape.setCenterY(595);
        }
        if (getYCenter() > 595) {
            shape.setCenterY(5);
        }
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public int getZIndex() {
        return 20;
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

    public void removeFromLevel(Level<?> l) {
        level.remove(this);

    }
}
