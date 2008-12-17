package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SlickButton;
import com.shade.levels.Level0;
import com.shade.resource.ResourceManager;
import com.shade.states.CreditState;

public class TitleState extends BasicGameState {

    public static final int ID = 2;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, highscores, credits;
    private int timer;

    public TitleState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
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
    
    public void reset() throws SlickException {
        master.control.load(new Level0(8, 6, 100));
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        initButtons();
        timer = 0;
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
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
                game.enterState(6, new FadeOutTransition(), null);
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
