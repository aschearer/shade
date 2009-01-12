package com.shade.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.FadeInImage;
import com.shade.controls.FadeInText;
import com.shade.controls.SlickButton;
import com.shade.score.FailSafeHighScoreReader;
import com.shade.score.FailSafeHighScoreWriter;
import com.shade.score.HighScoreWriter;
import com.shade.util.ResourceManager;

public class RecapState extends BasicGameState {

    private static final String PASS_TEXT = "Level Clear";
    private static final String FAIL_TEXT = "Level Failed";

    public static final int ID = 9;

    private MasterState master;
    private InGameState level;
    private ResourceManager resource;
    private SlickButton next, replay, back;

    private int levelScore, totalScore;

    private TextField input;
    private boolean noInternet;
    private FailSafeHighScoreReader reader;
    private HighScoreWriter writer;
    private ArrayList<FadeInText> scores;
    private ArrayList<FadeInImage> crowns;

    private boolean par;
    private StateBasedGame game;
    private boolean completed;

    public RecapState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("nextlevel-up", "states/recap/nextlevel-up.png");
        resource.register("nextlevel-down", "states/recap/nextlevel-down.png");
        resource.register("replay-up", "states/recap/replay-up.png");
        resource.register("replay-down", "states/recap/replay-down.png");

        scores = new ArrayList<FadeInText>();
        crowns = new ArrayList<FadeInImage>();
        reader = new FailSafeHighScoreReader();
        writer = new FailSafeHighScoreWriter();
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("RecapState was init'd!");
    }

    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        this.game = game;
        level = (InGameState) game.getState(InGameState.ID);
        par = level.parWasMet();
        initButtons();
        initScores();
        completed = false;
        initTextField(container);
        master.dimmer.rewind();
        levelScore = master.scorecard.getLevelScore();
        totalScore = master.scorecard.getScore();
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        if (par) {
            next.render(game, g);
        }
        replay.render(game, g);
        back.render(game, g);
        master.daisyXLarge.drawString(75, 135, (par) ? PASS_TEXT : FAIL_TEXT);

        master.jekyllMedium.drawString(90, 200, "Points Earned: " + levelScore);
        master.jekyllMedium.drawString(90, 225, "Total Points: " + totalScore);
        master.jekyllMedium.drawString(90, 280, "Distance Traveled: "
                + levelMileage());
        master.jekyllMedium.drawString(90, 305, "Total Distance: "
                + totalMileage());
        master.jekyllMedium.drawString(90, 360, "Mushrooms Collected: "
                + (int) level.stats.getStat("level-mushrooms"));
        master.jekyllMedium.drawString(90, 385, "Total Mushrooms: "
                + (int) level.stats.getStat("total-mushrooms"));
        // master.jekyllSmall.drawString(150, 275, "Tan: " + calculateTan());

        master.daisyLarge.drawString(450, 250, "Top 5 Scores");
        for (FadeInText t : scores) {
            t.render(game, g);
        }
        for (FadeInImage i : crowns) {
            i.render(game, g);
        }
        if (par && !completed) {
            master.jekyllMedium.drawString(470, 450, "Way to go champ!");
            input.render(container, g);
        }
        resource.get("trim").draw();
    }

    private String calculateTan() {
        float d = level.stats.getStat("level-mileage");
        float tan = d / (level.stats.getStat("level-damage") + 1);
        return tan + "";
    }

    public String totalMileage() {
        return (int) Math.floor(level.stats.getStat("total-mileage") / 10)
                + " feet";
    }

    public String levelMileage() {
        return (int) Math.floor(level.stats.getStat("level-mileage") / 10)
                + " feet";
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        if (par) {
            next.update(game, delta);
        }
        replay.update(game, delta);
        back.update(game, delta);
        for (FadeInText t : scores) {
            t.update(game, delta);
        }
        for (FadeInImage i : crowns) {
            i.update(game, delta);
        }
    }

    // @Override
    // public void keyPressed(int key, char c) {
    // if (key == Input.KEY_ENTER) {
    // level.nextLevel();
    // game.enterState(InGameState.ID, new FadeOutTransition(), null);
    // }
    // }

    private void initButtons() throws SlickException {
        initBackButton();
        initReplayButton();
        initNextButton();
    }

    private void initReplayButton() throws SlickException {
        int y = (par) ? 130 : 110;
        replay = new SlickButton(620, y, resource.get("replay-up"), resource
                .get("replay-down"));
        replay.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                level.currentLevel();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initNextButton() throws SlickException {
        next = new SlickButton(620, 110, resource.get("nextlevel-up"), resource
                .get("nextlevel-down"));
        next.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                level.nextLevel();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initBackButton() throws SlickException {
        int y = (par) ? 150 : 130;
        back = new SlickButton(620, y, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
                master.dimmer.reverse();
            }

        });
    }

    private void initScores() {
        scores.clear();
        crowns.clear();
        String[][] scoress = reader.getScores(level.getCurrentLevel(), 5);
        noInternet = reader.isLocal();
        int x = 470;
        int y = 300;
        int n = 0;
        for (String[] s : scoress) {
            if (s[2].equals("1")) {
                crowns.add(new FadeInImage(resource.get("crown"), x, y + 3
                        + (25 * n), 32, 20, 1000 + 400 * n));
            }
            scores.add(new FadeInText(s[0], master.jekyllMedium, x + 40, y
                    + (25 * n), 1000 + 400 * n));
            scores.add(new FadeInText(s[1], master.jekyllMedium, x + 180, y
                    + (25 * n), 1000 + 400 * n));
            n++;
        }
    }

    private void initTextField(GameContainer container) throws SlickException {
        int w = 200;
        int h = 32;
        int x = 470;
        int y = 480;
        input = new TextField(container, master.jekyllMedium, x, y, w, h);
        input.setMaxLength(8);
        input.setFocus(true);
        input.setAcceptingInput(par);

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
                            .getLevelScore(), level.getCurrentLevel(), false);
                    numTries--;
                }
                input.setAcceptingInput(false);
                completed = true;
                if (false) {
                    crowns.add(new FadeInImage(resource.get("crown"), 470, 428,
                            32, 20, 1000));
                }
                scores.add(new FadeInText(name, master.jekyllMedium,
                        510, 428, 1000));
                scores.add(new FadeInText(
                        master.scorecard.getLevelScore() + "",
                        master.jekyllMedium, 650, 428, 1000));
                // message = "Way to go " + input.getText() + "!! ... "
                // + randomResponse();
            }

        });
    }
}
