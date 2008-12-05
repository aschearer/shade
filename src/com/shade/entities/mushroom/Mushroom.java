package com.shade.entities.mushroom;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashLevel;
import com.shade.entities.Linkable;
import com.shade.entities.util.MushroomFactory;
import com.shade.lighting.LuminousEntity;

public class Mushroom extends Linkable {

    protected static final float SPEED = 1.6f;

    private static final float THRESHOLD = .6f;
    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .005f;
    private static final float MAX_SCALE = 3.5f;
    private static final float MIN_SCALE = 1.5f;

    protected enum States {
        SPAWNING, NORMAL, PICKED, COLLECTED
    };

    protected StateManager manager;
    protected float scale;

    private Image mushroom;
    private float luminosity;
    private MushroomFactory factory;
    private CrashLevel level;

    public Mushroom(float x, float y, MushroomFactory factory)
    throws SlickException {
        this.factory = factory;
        scale = 2;
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, RADIUS);
    }

    private void initResources() throws SlickException {
        SpriteSheet s;
        s = new SpriteSheet("entities/mushroom/mushrooms.png", 40, 40);
        mushroom = s.getSprite(0, 0);
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new SpawningShroom(this));
        manager.add(new NormalShroom(this));
        manager.add(new PickedShroom(this));
        manager.add(new CollectedShroom(this));
    }

    protected void kill() {
        detach();
        factory.remove(this);
        level.remove(this);
    }

    protected boolean inShadows() {
        return getLuminosity() <= THRESHOLD;
    }

    protected boolean tooBig() {
        return scale > MAX_SCALE;
    }

    protected boolean tooSmall() {
        return scale < MIN_SCALE;
    }

    protected void grow() {
        scale += SCALE_INCREMENT;
        resize();
    }

    protected void shrink() {
        scale -= SCALE_INCREMENT / 4;
        resize();
    }

    private void resize() {
        float x = shape.getCenterX();
        float y = shape.getCenterY();
        ((Circle) shape).setRadius(RADIUS * scale);
        shape.setCenterX(x);
        shape.setCenterY(y);
    }

    protected void draw() {
        mushroom.draw(getX(), getY(), getWidth(), getHeight());
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public int getZIndex() {
        return 1;
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
        // do nothing
    }

    public int getRole() {
        return manager.getRole();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
    }

    public void render(StateBasedGame game, Graphics g) {
        manager.render(game, g);
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
        testAndWrap();
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }
}
