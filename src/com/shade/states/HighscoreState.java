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
import com.shade.controls.FadeInImage;
import com.shade.controls.FadeInText;
import com.shade.controls.SlickButton;
import com.shade.util.ResourceManager;
import com.shade.score.FailSafeHighScoreReader;

public class HighscoreState extends BasicGameState {
    
    public static final int ID = 4;

    private static final String FEEDBACK_URL = "http://anotherearlymorning.com/shade/feedback";
    private static final String NO_INTERNET_MESSAGE = "Shade supports global high scores. Please connect to the internet to take advantage of this exciting feature.";

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, feedback, back;
    private int timer;
    private FailSafeHighScoreReader reader;
    private ArrayList<FadeInText> scores;
    private ArrayList<FadeInImage> crowns;

    private boolean noInternet;
    

    public HighscoreState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
        resource.register("crown", "states/highscore/crown.png");
        
        scores = new ArrayList<FadeInText>();
        crowns = new ArrayList<FadeInImage>();
        reader = new FailSafeHighScoreReader();
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
        if (master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        
        initScores();
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        play.render(game, g);
        feedback.render(game, g);
        back.render(game, g);
        resource.get("trim").draw();
        for (FadeInText t : scores) {
            t.render(game, g);
        }
        for (FadeInImage i : crowns) {
            i.render(game, g);
        }
        if (noInternet) {
            drawCentered(container, NO_INTERNET_MESSAGE, 550);
        }
    }
    
    private void drawCentered(GameContainer c, String s, int y) {
        int x = (c.getWidth() - master.jekyllSmall.getWidth(s)) / 2;
        master.jekyllSmall.drawString(x, y, s);
    }

    // render the aquarium
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            feedback.update(game, delta);
            back.update(game, delta);
        }
        master.dimmer.update(game, delta);
        for (FadeInText t : scores) {
            t.update(game, delta);
        }
        for (FadeInImage i : crowns) {
            i.update(game, delta);
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initFeedbackButton();
        initBackButton();
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
    
    private void initFeedbackButton() throws SlickException {
        feedback = new SlickButton(620, 130, resource.get("feedback-up"),
                resource.get("feedback-down"));
        feedback.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                BareBonesBrowserLaunch.openURL(FEEDBACK_URL);
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
    
    private void initScores() throws SlickException {
        scores.clear();
        crowns.clear();
        String[][] scoress = reader.getScores(0, 10);
        noInternet = reader.isLocal();
        int x = 50;
        int y = 100;
        int n = 0;
        for (String[] s : scoress) {
            if (s[2].equals("1")) {
                crowns.add(new FadeInImage(resource.get("crown"), x, y + 3 + (40 * n), 1000 + 400 *n));
            }
            scores.add(new FadeInText(s[0], master.jekyllLarge, x + 50, y + (40 * n), 1000 + 400 * n));
            scores.add(new FadeInText(s[1], master.jekyllLarge, x + 300, y + (40 * n), 1000 + 400 * n));
            n++;
        }
    }
}
