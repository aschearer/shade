package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.GameControl;
import com.shade.controls.SlickButton;
import com.shade.levels.Level0;
import com.shade.levels.Level1;
import com.shade.levels.Model;
import com.shade.lighting.LightMask;

public class TitleState extends BasicGameState {

    private static final int ID = 2;

    private Image header, background, trim;
    private GameControl control;

    private SlickButton play, highscores, credits;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        initButtons();

        LightMask view = new LightMask(5);
        Model model = new Level0(8, 6, 100);
        control = new GameControl(model, view);
    }

    private void initSprites() throws SlickException {
        header = new Image("states/common/header.png");
        background = new Image("states/common/background.png");
        trim = new Image("states/common/trim.png");
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initHighscoresButton();
        initCreditsButton();
    }

    private void initPlayButton() throws SlickException {
        Image up = new Image("states/title/play-up.png");
        Image down = new Image("states/title/play-down.png");
        play = new SlickButton(620, 110, up, down);
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID);
            }
            
        });
    }

    private void initHighscoresButton() throws SlickException {
        Image up = new Image("states/title/highscores-up.png");
        Image down = new Image("states/title/highscores-down.png");
        highscores = new SlickButton(620, 130, up, down);
    }

    private void initCreditsButton() throws SlickException {
        Image up = new Image("states/title/credits-up.png");
        Image down = new Image("states/title/credits-down.png");
        credits = new SlickButton(620, 150, up, down);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        control.render(game, g, background);
        header.draw(400, 0);
        play.render(game, g);
        highscores.render(game, g);
        credits.render(game, g);
        // titleFont.drawString(620, 110, "Play");
        // titleFont.drawString(620, 130, "Highscores");
        // titleFont.drawString(620, 150, "Credits");
        trim.draw();
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        control.update(game, delta);
        play.update(game, delta);
        highscores.update(game, delta);
        credits.update(game, delta);
    }

}
