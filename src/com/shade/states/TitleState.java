package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SlickButton;
import com.shade.levels.Shell;
import com.shade.util.ResourceManager;
import com.shade.states.CreditState;

public class TitleState extends BasicGameState {

    public static final int ID = 2;

    private static final String LEVEL_ZERO = "levels/level-0.xml";

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, highscores, credits;
    private int timer;

    public TitleState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        initButtons();
        reset();
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("TitleState was init'd!");
    }

    private void reset() throws SlickException {
        master.control.load(new Shell(LEVEL_ZERO));
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        timer = 0;
        initButtons();
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        play.render(game, g);
        highscores.render(game, g);
        credits.render(game, g);
        resource.get("trim").draw();
    }

    // render the aquarium
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        if (master.dimmer.reversed()) {
            master.dimmer.update(game, delta);
        }
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            highscores.update(game, delta);
            credits.update(game, delta);
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initHighscoresButton();
        initCreditsButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("play-up"), resource
                .get("play-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
            }

        });
    }

    private void initHighscoresButton() throws SlickException {
        highscores = new SlickButton(620, 130, resource.get("highscore-up"),
                resource.get("highscore-down"));
        highscores.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(HighscoreState.ID);
            }

        });
    }

    private void initCreditsButton() throws SlickException {
        credits = new SlickButton(620, 150, resource.get("credits-up"),
                resource.get("credits-down"));
        credits.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(CreditState.ID);
            }

        });
    }
}
