package com.shade.entities.mole;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashLevel;
import com.shade.entities.Linkable;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.util.MoleFactory;
import com.shade.lighting.LuminousEntity;

/**
 * The real deal; this mole is the sum of different mole states.
 *
 * No I haven't heard of encapsulation.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public final class Mole extends Linkable {

    protected enum States {
        SPAWNING, IDLE, WORKING
    }

    protected CrashLevel level;
    protected StateManager manager;
    protected Mushroom target;
    protected float heading;

    private MoleFactory factory;
    private float luminosity;

    public Mole(float x, float y, MoleFactory factory) throws SlickException {
        this.factory = factory;
        heading = (float) (Math.PI);
        initShape(x, y);
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 12f);
    }

    private void initStates() throws SlickException {
        manager = new StateManager();
        manager.add(new SpawningMole(this));
        manager.add(new IdleMole(this));
        manager.add(new WorkerMole(this));
    }

    protected boolean hasCollected(Mushroom m) {
        return contains(m);
    }

    protected void kill() {
        target = null;
        detachAll();
        factory.remove(this);
        level.remove(this);
    }


    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level < ? > l) {
        level = (CrashLevel) l;
    }

    public void removeFromLevel(Level < ? > l) {
        factory.remove(this);
    }

    public int getRole() {
        return manager.getRole();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getXCenter(), getYCenter(), (float) Math.toDegrees(heading));
        manager.render(game, g);
        g.resetTransform();
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public int getZIndex() {
        return 2;
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

    public int mushroomsCollected() {
        if (next == null) {
            return 0;
        }
        int i = 1;
        Linkable head = next;
        while (head.next != null) {
            i++;
            head = head.next;
        }
        return i;
    }
}
