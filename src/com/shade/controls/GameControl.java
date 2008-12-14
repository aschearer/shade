package com.shade.controls;

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
import com.shade.entities.util.MushroomFactory;
import com.shade.levels.Model;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.lighting.LuminousEntity;

public class GameControl {

    private Model model;
    private LightMask view;
    private GlobalLight globalLight;
    private MushroomCounter[] controls;

    private MushroomFactory factory;

    public GameControl(Model m, LightMask v, MushroomCounter... c) {
        model = m;
        view = v;
        controls = c;

        factory = model.getMushroomFactory();
        globalLight = model.getGlobalLight();
        view.add(globalLight);
        initPlayer();
        initBasket();
    }

    private void initPlayer() {
        Player p = (Player) model.getEntitiesByRole(Roles.PLAYER.ordinal())[0];
        for (MushroomCounter counter : controls) {
            if (counter instanceof MeterControl) {
                ((MeterControl) counter).track(p);
            }
        }
    }

    private void initBasket() {
        Basket b = (Basket) model.getEntitiesByRole(Roles.BASKET.ordinal())[0];
        for (MushroomCounter counter : controls) {
            b.add(counter);
        }
    }

    public void update(StateBasedGame game, int delta) throws SlickException {
        model.update(game, delta);
        globalLight.update(game, delta);
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

        while (globalLight.castShadow(e) == null) {
            e = randomEntity();
        }

        return globalLight.castShadow(e);
    }
}
