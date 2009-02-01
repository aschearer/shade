package com.shade.entities;

import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.controls.MushroomCounter;
import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

public class Basket extends Linkable {

    private static final int BASKET_WIDTH = 65;
    private static final int BASKET_HEIGHT = 40;
    private static final int BASKET_DEPTH = 0;
    private Image sprite;
    private float luminosity;
    private LinkedList<MushroomCounter> counters;

    public Basket(int x, int y) throws SlickException {
        initShape(x, y, BASKET_WIDTH, BASKET_HEIGHT);
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
        
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Roles.PICKED_MUSHROOM.ordinal()) {
            Mushroom m = (Mushroom) obstacle;
            notifyCounters(m);
            m.detach();
        }
        if(obstacle.getRole() == Roles.TREASURE.ordinal()){
            Mushroom m = (Mushroom) obstacle;
            //HACK TODO: KILL HACK! ACCESSS TREASURE STATE TO CHECK COLLECTION!
            if(m.prev!=null){
            m.detach();
            notifyCounters(m);
            }
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
        return BASKET_DEPTH;
    }

    public int compareTo(LuminousEntity l) {
        return getZIndex() - l.getZIndex();
    }

}
