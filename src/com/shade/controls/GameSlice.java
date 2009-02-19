package com.shade.controls;

import java.util.LinkedList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import sun.security.action.GetBooleanAction;

import com.crash.Body;
import com.crash.util.CrashGeom;
import com.shade.entities.Basket;
import com.shade.entities.Obstacle;
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
    // private boolean flushControls;

    // TIMER!
    private DayPhaseTimer timer;
    private Body basket;

    public GameSlice(LightMask v, GlobalLight l, DayPhaseTimer t) {
        view = v;
        light = l;
        timer = t;
        view.add(light);
        controls = new LinkedList<MushroomCounter>();
    }

    public void add(MushroomCounter c) {
        controls.add(c);
        initPlayer(c);
        initBaskets(c);
    }

    public void flushControls() {
        controls.clear();
    }

    public void load(Model m) {
        model = m;
        model.setTimer(timer);
        factory = m.getMushroomFactory();
        basket = (Body) model.getEntitiesByRole(Roles.BASKET.ordinal())[0];
    }

    public void addEntity(LuminousEntity e) {
        model.add(e);
    }

    public void update(StateBasedGame game, int delta) throws SlickException {
        model.update(game, delta);
        light.update(game, delta);
        // if (flushControls) {
        // controls.clear();
        // flushControls = false;
        // }
        for (int i = 0; i < controls.size(); i++) {
            controls.get(i).update(game, delta);
        }
        if (factory.active()) {
            GameContainer c = game.getContainer();
            Mushroom m = factory.getMushroom(c, randomShadow(), basket);
            if (m != null) {
                model.add(m);
            }
        }
        timer.update(delta);
    }

    public void render(StateBasedGame game, Graphics g, Image... backgrounds) {
        view.render(game, g, model.toArray(), backgrounds);
        for (MushroomCounter c : controls) {
            c.render(game, g);
        }
    }

    private LuminousEntity randomEntity() {
        Object[] entities = model.getEntitiesByRole(Roles.OBSTACLE.ordinal());
        int rank = (int) (Math.random() * Obstacle.maxRank);
        int i = (int) (Math.random() * entities.length);
        int counter = 0;
        while (((Obstacle) entities[i]).rank() < rank) {
            i = (int) (Math.random() * entities.length);
            counter++;
            if (counter % 2 == 0) {
                rank--;
            }
        }
//        System.out.println(entities[i]);
        return (LuminousEntity) entities[i];
    }

    private Shape randomShadow() {
        LuminousEntity e = randomEntity();
        Shape shadow = light.castShadow(e);

        while (shadow == null) {
            e = randomEntity();
            shadow = light.castShadow(e);
        }

        return shadow;
    }

    private void initPlayer(MushroomCounter counter) {
        if (!(counter instanceof MeterControl)) {
            return;
        }
        Object[] players = model.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (players.length == 0) {
            return;
        }
        Player p = (Player) players[0];
        ((MeterControl) counter).track(p);
    }

    private void initBaskets(MushroomCounter counter) {
        Object[] baskets = model.getEntitiesByRole(Roles.BASKET.ordinal());
        if (baskets.length == 0) {
            return;
        }
        for (Object o : baskets) {
            Basket b = (Basket) o;
            b.add(counter);
        }
        if (counter instanceof CounterControl) {
            ((CounterControl) counter).track(baskets[0]);
        }
    }

    public void killPlayer() {
        Object[] players = model.getEntitiesByRole(Roles.PLAYER.ordinal());
        if (players.length > 0) {
            model.remove((LuminousEntity) players[0]);
        }
    }

    public float distanceTraveled() {
        Object[] players = model.getEntitiesByRole(Roles.PLAYER.ordinal());
        return ((Player) players[0]).totalMileage();
    }
}
