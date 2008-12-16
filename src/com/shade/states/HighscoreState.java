package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SlickButton;

public class HighscoreState extends BasicGameState {

    public static final int ID = 3;

    private Image header, background, trim;
    private TitleState title;

    private SlickButton play, more, back;

    private int timer;

    private Image highscoreUp, highscoreDown;
    private Image playUp, playDown;
    private Image backUp, backDown;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        initButtons();
    }

    private void initSprites() throws SlickException {
        header = new Image("states/common/header.png");
        background = new Image("states/common/background.png");
        trim = new Image("states/common/trim.png");
        
        playUp = new Image("states/common/play-up.png");
        playDown = new Image("states/common/play-down.png");

        highscoreUp = new Image("states/highscore/more-up.png");
        highscoreDown = new Image("states/highscore/more-down.png");

        backUp = new Image("states/common/back-up.png");
        backDown = new Image("states/common/back-down.png");
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initMoreButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, playUp, playDown);
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID);
            }

        });
    }

    private void initMoreButton() throws SlickException {
        more = new SlickButton(620, 130, highscoreUp, highscoreDown);
        more.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                // TODO launch browser
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, backUp, backDown);
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
            }

        });
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        timer = 0;
        title = (TitleState) game.getState(TitleState.ID);
        // TODO see TitleState for this bug... (hammer time?)
        initButtons();
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        title.control.render(game, g, background);
        header.draw(400, 0);
        trim.draw();
        play.render(game, g);
        more.render(game, g);
        back.render(game, g);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        timer += delta;
        title.control.update(game, delta);
        if (timer > 200) {
            play.update(game, delta);
            more.update(game, delta);
            back.update(game, delta);
        }
    }

}
