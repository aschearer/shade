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

public class HighscoreState extends BasicGameState {
    
    public static final int ID = 4;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, morescores, back;
    private int timer;

    public HighscoreState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        
        resource.register("more-up", "states/highscore/more-up.png");
        resource.register("more-down", "states/highscore/more-down.png");
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("HighscoreState was init'd!");
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
        morescores.render(game, g);
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
            morescores.update(game, delta);
            back.update(game, delta);
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initMoreScoresButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("play-up"), resource
                .get("play-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initMoreScoresButton() throws SlickException {
        morescores = new SlickButton(620, 130, resource.get("more-up"),
                resource.get("more-down"));
        morescores.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                // TODO launch browser
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, resource.get("back-up"),
                resource.get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
            }

        });
    }
}
