package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.GameControl;
import com.shade.controls.SlickButton;
import com.shade.levels.Level1;
import com.shade.levels.Model;
import com.shade.lighting.LightMask;

public class TitleState extends BasicGameState {

    public static final int ID = 2;

    private Image header, background, trim;
    
    private Image playUp, playDown;
    private Image highscoreUp, highscoreDown;
    private Image creditsUp, creditsDown;

    private SlickButton play, highscores, credits;
    protected GameControl control;

    private int timer;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        initButtons();

        LightMask view = new LightMask(5);
        Model model = new Level1(8, 6, 100);
        control = new GameControl(model, view);
    }

    private void initSprites() throws SlickException {
        header = new Image("states/common/header.png");
        background = new Image("states/common/background.png");
        trim = new Image("states/common/trim.png");

        playUp = new Image("states/common/play-up.png");
        playDown = new Image("states/common/play-down.png");

        highscoreUp = new Image("states/title/highscores-up.png");
        highscoreDown = new Image("states/title/highscores-down.png");

        creditsUp = new Image("states/title/credits-up.png");
        creditsDown = new Image("states/title/credits-down.png");
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initHighscoresButton();
        initCreditsButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, playUp, playDown);
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initHighscoresButton() throws SlickException {
        highscores = new SlickButton(620, 130, highscoreUp, highscoreDown);
        highscores.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(HighscoreState.ID);
            }

        });
    }

    private void initCreditsButton() throws SlickException {
        credits = new SlickButton(620, 150, creditsUp, creditsDown);
        credits.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(CreditState.ID);
            }

        });
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        timer = 0;
        // TODO some wierd bug if you just reset, hammer time!
        initButtons();
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        control.render(game, g, background);
        header.draw(400, 0);
        play.render(game, g);
        highscores.render(game, g);
        credits.render(game, g);
        trim.draw();
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        control.update(game, delta);
        timer += delta;
        if (timer > 200) {
            play.update(game, delta);
            highscores.update(game, delta);
            credits.update(game, delta);
        }
    }

}
