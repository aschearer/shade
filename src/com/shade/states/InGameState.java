package com.shade.states;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.controls.CounterControl;
import com.shade.controls.GameControl;
import com.shade.controls.MeterControl;
import com.shade.levels.LevelManager;
import com.shade.levels.Model;
import com.shade.lighting.LightMask;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image background, trim, counterSprite;
    private TrueTypeFont counterFont;
    private LevelManager manager;
    private GameControl control;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initFonts();
        initSprites();

        manager = new LevelManager(8, 6, 100);

        Model model = manager.next();
        LightMask view = new LightMask(5);

        MeterControl meter = new MeterControl(20, 456, 100, 100);
        CounterControl counter = new CounterControl(60, 520, counterSprite,
                counterFont);

        control = new GameControl(model, view, meter, counter);
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

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        control.render(game, g, background);
        trim.draw();
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        control.update(game, delta);
    }

}
