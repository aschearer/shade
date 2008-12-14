package com.shade.states;

import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
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

    private MeterControl meter;
    private CounterControl counter;

    private int timer;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initFonts();
        initSprites();

        manager = new LevelManager(8, 6, 100);

        meter = new MeterControl(20, 456, 100, 100);
        counter = new CounterControl(60, 520, counterSprite, counterFont);
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        super.enter(container, game);
        nextLevel(game);
        timer = 0;
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
        if (timer < 4000) {
            drawCenteredBanner(container, g, 100);
            if (timer < 3500) {
                writeCentered(container, "Get Ready...");
            } else {
                writeCentered(container, "Go!");
            }
        }
    }
    
    private void drawCenteredBanner(GameContainer c, Graphics g, int height) { 
        g.fillRect(0, c.getHeight() / 2 - height / 2, c.getWidth(), height);
    }

    private void writeCentered(GameContainer c, String m) {
        int w = counterFont.getWidth(m);
        int h = counterFont.getHeight();
        int x = c.getWidth() / 2 - w / 2;
        int y = c.getHeight() / 2 - h / 2;
        counterFont.drawString(x, y, m, Color.black);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        timer += delta;
        if (timer > 4000) {
            control.update(game, delta);
            if (control.levelClear()) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }
        }
    }

    private void nextLevel(StateBasedGame game) {
        if (manager.hasNext()) {
            LightMask view = new LightMask(5);
            Model model = manager.next();
            control = new GameControl(model, view, meter, counter);
        } else {
            // TODO go to credits state.
        }
    }

}
