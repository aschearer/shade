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
import com.shade.resource.ResourceManager;

public class EnterScoreState extends BasicGameState {

    public static final int ID = 6;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, highscores, back;
    private int timer;

    public EnterScoreState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        
        resource.register("playagain-up", "states/enter/playagain-up.png");
        resource.register("playagain-down", "states/enter/playagain-down.png");
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("EnterScoreState was init'd!");
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
        back.render(game, g);
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
            back.update(game, delta);
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initHighscoresButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("playagain-up"), resource
                .get("playagain-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
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

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
            }

        });
    }
}
