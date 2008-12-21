package com.shade.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.FadeInText;
import com.shade.controls.SlickButton;
import com.shade.resource.ResourceManager;
import com.shade.score.HighScoreReader;
import com.shade.score.RemoteHighScoreReader;

public class HighscoreState extends BasicGameState {
    
    public static final int ID = 4;
    
    private static final String HIGHSCORE_URL = "http://anotherearlymorning.com/";

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, morescores, back;
    private int timer;
    private HighScoreReader reader;
    private ArrayList<FadeInText> scores;
    

    public HighscoreState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        
        resource.register("more-up", "states/highscore/more-up.png");
        resource.register("more-down", "states/highscore/more-down.png");
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
        
        scores = new ArrayList<FadeInText>();
        reader = new RemoteHighScoreReader("http://anotherearlymorning.com/games/shade/board.php");
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
        scores.clear();
        if (!master.dimmer.finished()) {
            master.dimmer.reset();
        }
        
        String[] scoress = reader.getScores(10);
        int x = 50;
        int y = 100;
        int n = 0;
        for (String s : scoress) {
            String[] score = s.split(",");
            scores.add(new FadeInText(score[0], master.jekyllLarge, x, y + (40 * n), 1000 + 400 * n));
            scores.add(new FadeInText(score[1], master.jekyllLarge, x + 300, y + (40 * n), 1000 + 400 * n));
            n++;
        }
        
        
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        play.render(game, g);
        morescores.render(game, g);
        back.render(game, g);
        resource.get("trim").draw();
        for (FadeInText t : scores) {
            t.render(game, g);
        }
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
        master.dimmer.update(game, delta);
        for (FadeInText t : scores) {
            t.update(game, delta);
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
                game.enterState(InstructionState.ID);
            }

        });
    }

    private void initMoreScoresButton() throws SlickException {
        morescores = new SlickButton(620, 130, resource.get("more-up"),
                resource.get("more-down"));
        morescores.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                BareBonesBrowserLaunch.openURL(HIGHSCORE_URL);
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, resource.get("back-up"),
                resource.get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
                master.dimmer.reverse();
            }

        });
    }
}
