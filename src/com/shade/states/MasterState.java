package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.GameSlice;
import com.shade.controls.ScoreControl;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.resource.ResourceManager;

public class MasterState extends BasicGameState {

    public static final int ID = 1;

    public static final int STATE_TRANSITION_DELAY = 200;
    public static final int SECONDS_PER_DAY = 60000;

    public ResourceManager resource;
    public GameSlice control;
    public ScoreControl scorecard;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        resource = new ResourceManager();
        // register resources
        resource.register("header", "states/common/header.png");
        resource.register("background", "states/common/background.png");
        resource.register("trim", "states/common/trim.png");

        resource.register("play-up", "states/common/play-up.png");
        resource.register("play-down", "states/common/play-down.png");
        resource.register("highscore-up", "states/title/highscores-up.png");
        resource.register("highscore-down", "states/title/highscores-down.png");
        resource.register("credits-up", "states/title/credits-up.png");
        resource.register("credits-down", "states/title/credits-down.png");

        // create controller
        control = new GameSlice(new LightMask(5), createLight());

        // register states
        game.addState(new TitleState(this));
        game.addState(new InGameState(this));
        game.addState(new HighscoreState(this));
        game.addState(new CreditState(this));
        game.addState(new EnterScoreState(this));

    }

    private GlobalLight createLight() {
        return new GlobalLight(12, (float) (4 * Math.PI / 3), SECONDS_PER_DAY);
    }

    // render splash and loading screens
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        // TODO Auto-generated method stub

    }

    // render splash and loading screens
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        game.enterState(TitleState.ID);
    }

}
