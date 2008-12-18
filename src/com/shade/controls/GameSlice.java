package com.shade.controls;

import java.util.LinkedList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.Basket;
import com.shade.entities.Player;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.mushroom.MushroomFactory;
import com.shade.levels.Model;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.lighting.LuminousEntity;

public class GameSlice {

    private GlobalLight light;
    private Model model;
    private MushroomFactory factory;
    private LightMask view;
    private LinkedList<MushroomCounter> controls;
    private boolean flushControls;

    public GameSlice(LightMask v, GlobalLight l) {
        view = v;
        light = l;
        view.add(light);
        controls = new LinkedList<MushroomCounter>();
    }
    
    public void add(MushroomCounter c) {
        controls.add(c);
    }
    
    public void flushControls() {
        flushControls = true;
    }

    public void load(Model m) {
        model = m;
        factory = m.getMushroomFactory();
        initPlayer();
        initBasket();
    }
    
    public void update(StateBasedGame game, int delta) throws SlickException {
        model.update(game, delta);
        light.update(game, delta);
        if (flushControls) {
            controls.clear();
            flushControls = false;
        }
        for (MushroomCounter c : controls) {
            c.update(game, delta);
        }
        if (factory.active()) {
            GameContainer c = game.getContainer();
            Mushroom m = factory.getMushroom(c, randomShadow());
            if (m != null) {
                model.add(m);
            }
        }
    }

    public void render(StateBasedGame game, Graphics g, Image ... backgrounds) {
        view.render(game, g, model.toArray(), backgrounds);
        for (MushroomCounter c : controls) {
            c.render(game, g);
        }
    }

    private LuminousEntity randomEntity() {
        LuminousEntity[] entities = model.toArray(new LuminousEntity[0]);
        int i = (int) (Math.random() * entities.length);
        return entities[i];
    }

    private Shape randomShadow() {
        LuminousEntity e = randomEntity();

        while (light.castShadow(e) == null) {
            e = randomEntity();
        }

        return light.castShadow(e);
    }

    private void initPlayer() {
        Object[] players = model.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (players.length == 0) {
            return;
        }
        Player p = (Player) players[0];
        for (MushroomCounter counter : controls) {
            if (counter instanceof MeterControl) {
                ((MeterControl) counter).track(p);
            }
        }
    }

    private void initBasket() {
        Object[] baskets = model.getEntitiesByRole(Roles.BASKET.ordinal());
        if (baskets.length == 0) {
            return;
        }
        Basket b = (Basket) baskets[0];
        for (MushroomCounter counter : controls) {
            b.add(counter);
        }
    }

    public void killPlayer() {
        Object[] players = model.getEntitiesByRole(Roles.PLAYER.ordinal());
        model.remove((LuminousEntity) players[0]);
    }
}
