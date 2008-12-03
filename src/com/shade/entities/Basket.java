package com.shade.entities;

import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.controls.MushroomCounter;
import com.shade.lighting.LuminousEntity;

public class Basket extends Linkable {

    private Image sprite;
    private float luminosity;
    private LinkedList<MushroomCounter> counters;
    private Level<LuminousEntity> level;

    public Basket(float x, float y, float w, float h) throws SlickException {
        initShape(x, y, w, h);
        initSprite();
        counters = new LinkedList<MushroomCounter>();
    }

    private void initSprite() throws SlickException {
        sprite = new Image("entities/basket/basket.png");
    }

    private void initShape(float x, float y, float w, float h) {
        shape = new Rectangle(x, y, w, h);
    }

    public void add(MushroomCounter c) {
        counters.add(c);
    }

    @SuppressWarnings("unchecked")
    public void addToLevel(Level < ? > l) {
        level = (Level<LuminousEntity>) l;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.MUSHROOM.ordinal()) {
            Mushroom m = (Mushroom) obstacle;
            m.detach();
            level.remove(m);
            notifyCounters(m);
        }
    }

    private void notifyCounters(Mushroom m) {
        for (MushroomCounter c : counters) {
            c.onCollect(m);
        }
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public int getRole() {
        return Roles.BASKET.ordinal();
    }


    @SuppressWarnings("unchecked")
    public void removeFromLevel(Level l) {
        // basket doesn't need this
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(getX(), getY(), getWidth(), getHeight());
        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        // basket doesn't need this
    }

    public int getZIndex() {
        return 1;
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
