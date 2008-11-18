package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Level;
import com.shade.crash.CrashLevel;
import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.entities.util.MushroomFactory;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.lighting.LightSourceProxy;
import com.shade.lighting.LuminousEntity;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image background;
    private LightMask view;
    private Level<LuminousEntity> model;
    private LightSourceProxy lights;
    private MushroomFactory factory;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
    throws SlickException {
        lights = new LightSourceProxy();
        lights.add(new GlobalLight(12, (float) (4 * Math.PI / 3)));

        view = new LightMask(6);
        view.add(lights);
        background = new Image("states/ingame/background.png");

        model = new CrashLevel<LuminousEntity>(8, 6, 100);
        model.add(new Player(300, 200));
        model.add(new Basket(400, 250, 65, 40));
        // blocks
        model.add(new Block(55, 355, 125, 125, 16));
        model.add(new Block(224, 424, 56, 56, 6));
        model.add(new Block(324, 424, 56, 56, 6));
        model.add(new Block(75, 225, 56, 56, 6));
        model.add(new Block(545, 330, 80, 80, 10));
        model.add(new Block(445, 460, 80, 80, 10));
        // domes
        model.add(new Dome(288, 165, 32, 7));
        model.add(new Dome(180, 95, 44, 10));
        model.add(new Dome(300, 65, 25, 6));
        model.add(new Dome(710, 80, 28, 6));
        model.add(new Dome(600, 100, 40, 9));
        model.add(new Dome(680, 220, 60, 13));
        // fences
        model.add(new Fence(250, 250, 11, 120, 5));
        model.add(new Fence(390, 140, 120, 11, 5));
        model.add(new Fence(715, 368, 11, 120, 5));
        model.add(new Fence(50, 50, 11, 120, 5));

        factory = new MushroomFactory(8, .001);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
    throws SlickException {
        view.render(game, g, model.toArray(new LuminousEntity[0]), background);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
    throws SlickException {
        model.update(game, delta);
        lights.update(game, delta);
        if (factory.active()) {
            //model.add(factory.getMushroom(container));
        }
    }

}
