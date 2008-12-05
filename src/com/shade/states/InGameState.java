package com.shade.states;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.base.Level;
import com.shade.controls.CounterControl;
import com.shade.controls.MeterControl;
import com.shade.entities.Basket;
import com.shade.entities.Player;
import com.shade.entities.mole.Mole;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.Roles;
import com.shade.entities.util.MoleFactory;
import com.shade.entities.util.MushroomFactory;
import com.shade.levels.LevelManager;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.lighting.LightSourceProxy;
import com.shade.lighting.LuminousEntity;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image background, trim, counterSprite;
    private TrueTypeFont counterFont;
    private MeterControl meter;
    private CounterControl counter;
    private LightMask view;
    private Player player;
    private LevelManager manager;
    private Level<LuminousEntity> model;
    private GlobalLight globalLight;
    private LightSourceProxy lights;
    private MushroomFactory factory;
    private MoleFactory mfactory;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
    throws SlickException {
        initFonts();
        initSprites();
        initControls();

        lights = new LightSourceProxy();
        globalLight = new GlobalLight(12, (float) (4 * Math.PI / 3));
        lights.add(globalLight);

        view = new LightMask(5);
        view.add(lights);

        manager = new LevelManager(8, 6, 100);

        model = manager.next();

        player = (Player) model.getEntitiesByRole(Roles.PLAYER.ordinal())[0];

        Basket b = (Basket) model.getEntitiesByRole(Roles.BASKET.ordinal())[0];
        b.add(counter);
        b.add(meter);

        factory = new MushroomFactory(8, .001);
        mfactory = new MoleFactory(1);
    }

    private void initFonts() throws SlickException {
        try {
            InputStream oi = ResourceLoader
                             .getResourceAsStream("states/ingame/jekyll.ttf");
            Font jekyll = Font.createFont(Font.TRUETYPE_FONT, oi);
            counterFont = new TrueTypeFont(jekyll.deriveFont(36f), true);
        } catch (Exception e) {
            throw new SlickException("Failed to load font.", e);
        }
    }

    private void initSprites() throws SlickException {
        background = new Image("states/ingame/background.png");
        trim = new Image("states/ingame/trim.png");
        counterSprite = new Image("states/ingame/counter.png");
    }

    private void initControls() throws SlickException {
        meter = new MeterControl(20, 456, 100, 100);
        counter = new CounterControl(60, 520, counterSprite, counterFont);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
    throws SlickException {
        view.render(game, g, model.toArray(new LuminousEntity[0]), background);
        trim.draw();
        counter.render(game, g);
        meter.render(game, g);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
    throws SlickException {
        model.update(game, delta);
        lights.update(game, delta);
        counter.update(game, delta);
        meter.update(game, delta);
        if (factory.active()) {
            Mushroom m = factory.getMushroom(container, randomShadow());
            if (m != null) {
                model.add(m);
            }
        }

        if (mfactory.active()) {
            Mole m = mfactory.getMole(container, randomShadow());
            if (m != null) {
                model.add(m);
            }
        }

        if (player.getLuminosity() > .6) {
            meter.decrement(.1f);
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
