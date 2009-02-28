package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SerialStats;
import com.shade.controls.SlickButton;
import com.shade.util.ResourceManager;
import com.shade.score.FailSafeHighScoreWriter;
import com.shade.score.HighScoreWriter;

public class EnterScoreState extends BasicGameState {

    private static final String PROMPT_WINNER = "Way to go! Er... what's your name?";
    private static final String PROMPT_LOSER = "Nice try! Er... what's your name?";
    private static final String[] RESPONSES = { "(Is that really a name?)",
            "Never heard of ya.", "Bet you can't beat me!",
            "Buffer Overflow at line 6.", "Cool guy, huh?",
            "You beat little Johnny!", "Go tell your friends!" };

    public static final int ID = 6;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, highscores, back;
    private int timer;
    private TextField input;
    private String message;
    private HighScoreWriter writer;
    private boolean completed;

    // private Music badEnding, goodEnding;
    // private boolean played;

    public EnterScoreState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("playagain-up", "states/enter/playagain-up.png");
        resource.register("playagain-down", "states/enter/playagain-down.png");
        resource.register("losers-wreath", "states/enter/losers-wreath.png");
        resource.register("winners-wreath", "states/enter/winners-wreath.png");
        writer = new FailSafeHighScoreWriter();
        // badEnding = new Music("states/enter/loser.ogg", true);
        // goodEnding = new Music("states/enter/winner.ogg", true);
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
        if (master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        completed = false;
        initTextField(container);
        message = (master.scorecard.isGameBeaten()) ? PROMPT_WINNER
                : PROMPT_LOSER;
        // master.music.fade(500, 0, true);
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("winners-wreath").drawCentered(400, 260);
        drawScore(container, master.scorecard.getScore() + "", 218);
        if (!completed) {
            input.render(container, g);
        }
        drawCentered(container, message, 440);
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
        master.dimmer.update(game, delta);
        timer += delta;
        // if (!played && !master.music.playing()) {
        // played = true;
        // if (master.scorecard.isCleared()) {
        // goodEnding.setVolume(0);
        // goodEnding.play();
        // goodEnding.fade(1000, 1, false);
        // } else {
        // badEnding.setVolume(.5f);
        // badEnding.play();
        // badEnding.fade(1000, 1, false);
        // }
        // }
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            highscores.update(game, delta);
            back.update(game, delta);
        }
    }

    private void drawCentered(GameContainer c, String s, int y) {
        int x = (c.getWidth() - master.daisyMedium.getWidth(s)) / 2;
        master.daisyMedium.drawString(x, y, s);
    }

    private void drawScore(GameContainer c, String s, int y) {
        int x = (c.getWidth() - master.daisyLarge.getWidth(s)) / 2;
        master.daisyLarge.drawString(x, y, s);
    }

    private void initTextField(GameContainer container) throws SlickException {
        int w = 320;
        int h = 40;
        int x = (container.getWidth() - w) / 2;
        int y = (container.getHeight() - h) / 2 + 100;
        input = new TextField(container, master.jekyllLarge, x, y, w, h);
        input.setMaxLength(12);
        input.setFocus(true);

        input.addListener(new ComponentListener() {

            public void componentActivated(AbstractComponent c) {
                String name = input.getText().trim();
                if (name.equals("")) {
                    name = "Anon";
                }
                int numTries = 3;
                boolean written = false;
                while (!written && numTries > 0) {
                    written = writer.write(name, master.scorecard
                            .getScore(), 0, SerialStats.allClear());
                    numTries--;
                }
                input.setAcceptingInput(false);
                completed = true;
                message = "Way to go " + input.getText() + "!! ... "
                        + randomResponse();
            }

        });
    }

    private String randomResponse() {
        int r = (int) (Math.floor(Math.random() * RESPONSES.length));
        return RESPONSES[r];
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
                game.enterState(SelectState.ID);
                // master.music.play();
                // master.music.fade(1000, 1f, false);
                // played = false;
            }

        });
    }

    private void initHighscoresButton() throws SlickException {
        highscores = new SlickButton(620, 130, resource.get("highscore-up"),
                resource.get("highscore-down"));
        highscores.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(HighscoreState.ID);
                // master.music.play();
                // master.music.fade(1000, 1f, false);
                // played = false;
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
                // master.music.play();
                // master.music.fade(1000, 1f, false);
                // played = false;
                master.dimmer.reverse();
            }

        });
    }
}
