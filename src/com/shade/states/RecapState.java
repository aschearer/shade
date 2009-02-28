package com.shade.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.base.Animatable;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.FadeInImage;
import com.shade.controls.FadeInText;
import com.shade.controls.KeyListener;
import com.shade.controls.SerialStats;
import com.shade.controls.SlickButton;
import com.shade.controls.StatMeter;
import com.shade.controls.TwoToneButton;
import com.shade.levels.LevelManager;
import com.shade.score.FailSafeHighScoreReader;
import com.shade.score.FailSafeHighScoreWriter;
import com.shade.score.HighScoreWriter;
import com.shade.util.ResourceManager;

public class RecapState extends BasicGameState {

    /* Hypothetical top score. */
    private static final int MAX_LEVEL_SCORE = 16000;
    private static final int MAX_LEVEL_COUNT = 80;
    private static final int MAX_GOLDEN_COUNT = 10;
    private static final String PASS_TEXT = "Level Clear";
    private static final String FAIL_TEXT = "Level Failed";

    public static final int ID = 9;

    private MasterState master;
    private InGameState level;
    private ResourceManager resource;
    private SlickButton nextLevel, replay, back;
    private TwoToneButton next, prev;

    private boolean completed;
    private String name;
    private FailSafeHighScoreReader reader;
    private HighScoreWriter writer;

    private boolean par;
    private StateBasedGame game;
    private int index;
    private int timer, lockFlipper;
    private String message;
    private StatGizmo stats;
    private ScoreGizmo scores;
    private InputGizmo input;
    private SpriteSheet statsIcons;
    private boolean unlocked;

    public RecapState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("gameover-up", "states/recap/gameover-up.png");
        resource.register("gameover-down", "states/recap/gameover-down.png");
        resource.register("nextlevel-up", "states/recap/nextlevel-up.png");
        resource.register("nextlevel-down", "states/recap/nextlevel-down.png");
        resource.register("replay-up", "states/recap/replay-up.png");
        resource.register("replay-down", "states/recap/replay-down.png");
        resource.register("levels-up", "states/recap/levels-up.png");
        resource.register("levels-down", "states/recap/levels-down.png");
        resource.register("wreath", "states/recap/wreath.png");
        resource.register("unlocked", "states/recap/unlocked.png");
        statsIcons = new SpriteSheet("states/recap/icons.png", 40, 40);

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
        timer = 0;
        lockFlipper = 0;
        index = 0;
        level = (InGameState) game.getState(InGameState.ID);
        par = level.parWasMet();
        unlocked = master.levelsLock.newLevelUnlocked();
        master.levelsLock.testAndUnlockLevels();
        if (master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        message = (par) ? PASS_TEXT : FAIL_TEXT;
        initButtons();
        initFlowButtons();
        initStats();
        initScores();
        initInput(container);
        // initScores();
        // completed = false;
        // initTextField(container);
        // master.dimmer.rewind();
        // levelScore = master.scorecard.getLevelScore();
        // totalScore = master.scorecard.getScore();
    }

    private void initInput(GameContainer container) {
        input = new InputGizmo(container);
    }

    private void initStats() {
        try {
            stats = new StatGizmo();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    private void initScores() {
        scores = new ScoreGizmo();
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        if (par && !completed && index == 1) {
            resource.get("wreath").draw(205, 110);
        }
        resource.get("header").draw(400, 0);
        resource.get("backdrop").draw(0, 400);
        input.render(game, g);
        stats.render(game, g);
        scores.render(game, g);
        if (validNext()) {
            nextLevel.render(game, g);
        }

        if (par && unlocked) {
            resource.get("unlocked").draw(15, 412, 32, 32);
            if (lockFlipper > 300) {
                master.jekyllXSmall.drawString(50, 420, "New level unlocked!");
                if (lockFlipper > 700) {
                    lockFlipper = 0;
                }
            }
        }
        replay.render(game, g);
        back.render(game, g);
        prev.render(game, g);
        next.render(game, g);
        renderInstructionStep();
        resource.get("trim").draw();
    }

    private void drawCentered(String m) {
        float w = master.daisyXLarge.getWidth(m) / 2;
        master.daisyXLarge.drawString(400 - w, 440, m);
    }

    private void renderInstructionStep() {
        int num = (par) ? 2 : 1;
        master.jekyllXSmall.drawString(18, 495, (1 + index) + " of " + num);
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

    public int levelMileage() {
        return (int) Math.floor(level.stats.getStat("level-mileage") / 10);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        stats.update(game, delta);
        scores.update(game, delta);
        input.update(game, delta);
        lockFlipper += delta;
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            if (validNext()) {
                nextLevel.update(game, delta);
            }
            replay.update(game, delta);
            back.update(game, delta);
            prev.update(game, delta);
            next.update(game, delta);
        }
        prev.active(index == 0);
        next.active(index == 1);
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
        int y = (validNext()) ? 130 : 110;
        replay = new SlickButton(620, y, resource.get("replay-up"), resource
                .get("replay-down"));
        replay.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                level.currentLevel();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private boolean validNext() {
        return par && (level.getCurrentLevel() < LevelManager.NUM_LEVELS - 1 &&
                master.levelsLock.isUnlocked(level.getCurrentLevel() + 1) ||
                (level.getCurrentLevel() == LevelManager.NUM_LEVELS - 1));
    }

    private void initNextButton() throws SlickException {
        if (validNext()) {
            if (level.getCurrentLevel() == LevelManager.NUM_LEVELS - 1) {
                nextLevel = new SlickButton(620, 110, resource.get("gameover-up"),
                        resource.get("gameover-down"));
            } else {
                nextLevel = new SlickButton(620, 110, resource.get("nextlevel-up"),
                    resource.get("nextlevel-down"));
            }
            nextLevel.addListener(new ClickListener() {
    
                public void onClick(StateBasedGame game, Button clicked) {
                    if (level.getCurrentLevel() == LevelManager.NUM_LEVELS - 1) {
                        game.enterState(EnterScoreState.ID);
                    } else {
                        level.nextLevel();
                        game.enterState(InGameState.ID, new FadeOutTransition(),
                                null);
                    }
                }
    
            });
        }
    }

    private void initBackButton() throws SlickException {
        int y = (validNext()) ? 150 : 130;
        back = new SlickButton(620, y, resource.get("levels-up"), resource
                .get("levels-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
                master.dimmer.reverse();
            }

        });
    }

    private void initFlowButtons() throws SlickException {
        Image up = resource.get("next-up");
        Image down = resource.get("next-down");
        next = new TwoToneButton(760, 490, up, down);
        prev = new TwoToneButton(740, 490, up.getFlippedCopy(true, false), down
                .getFlippedCopy(true, false));

        next.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                index++;
                evaluateState();
            }

        });

        next.register(Input.KEY_RIGHT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                index++;
                evaluateState();
            }

        });

        prev.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                index--;
                evaluateState();
            }

        });

        prev.register(Input.KEY_LEFT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                index--;
                evaluateState();
            }

        });

    }

    private void evaluateState() {
        switch (index) {
            case 0:
                stats.show(true);
                scores.show(false);
                input.show(false);
                break;
            case 1:
                scores.show(completed || !par);
                stats.show(false);
                input.show(!completed && par);
                break;
            default:
                stats.show(false);
                break;
        }
    }

    private class InputGizmo implements Animatable {

        private boolean show;
        private TextField input;

        public InputGizmo(GameContainer container) {
            show = false;
            completed = false;
            name = "";
            initTextField(container);
        }

        public void render(StateBasedGame game, Graphics g) {
            if (show) {
                drawScore(master.scorecard.getLevelScore() + "");
                input.render(game.getContainer(), g);
            }
        }

        private void drawScore(String score) {
            float x = 400 - master.daisyLarge.getWidth(score) / 2;
            master.daisyLarge.drawString(x, 210, score);
        }

        public void update(StateBasedGame game, int delta) {

        }

        private void initTextField(GameContainer container) {
            int w = 200;
            int h = 32;
            int x = 300;
            int y = 440;
            input = new TextField(container, master.jekyllMedium, x, y, w, h);
            input.setMaxLength(12);
            input.setAcceptingInput(false);
            input.setFocus(par);
            input.setBorderColor(null);

            input.addListener(new ComponentListener() {

                public void componentActivated(AbstractComponent c) {
                    name = input.getText().trim();
                    if (name.equals("")) {
                        name = "Anon";
                    }
                    int numTries = 3;
                    boolean written = false;
                    while (!written && numTries > 0) {
                        written = writer.write(name, master.scorecard
                                .getLevelScore(), level.getCurrentLevel(),
                                SerialStats.allClear());
                        writer.write(name, master.scorecard
                                .getScore(), 0,
                                master.levelsLock.allUnlocked());
                        numTries--;
                    }
                    input.setAcceptingInput(false);
                    completed = true;
                    show(false);
                    scores.show(true);
                }

            });
        }

        public void show(boolean b) {
            show = b;
            input.setAcceptingInput(b );
        }

    }

    private class ScoreGizmo implements Animatable {

        private boolean show;
        private ArrayList<FadeInText> scores;
        private ArrayList<FadeInImage> crowns;

        public ScoreGizmo() {
            scores = new ArrayList<FadeInText>();
            crowns = new ArrayList<FadeInImage>();
            show = false;
        }

        public void render(StateBasedGame game, Graphics g) {
            if (show) {
                master.daisyLarge.drawString(240, 175, "Top 5 Scores");
                for (FadeInImage crown : crowns) {
                    crown.render(game, g);
                }
                for (FadeInText score : scores) {
                    score.render(game, g);
                }
                if (par) {
                    drawCongrat("Way to go " + name);
                } else {
                    drawCongrat("Better luck next time");
                }
            }
        }

        private void drawCongrat(String m) {
            float x = 400 - master.daisyLarge.getWidth(m) / 2;
            master.daisyLarge.drawString(x, 440, m);
        }

        public void update(StateBasedGame game, int delta) {
            if (show) {
                for (FadeInImage crown : crowns) {
                    crown.update(game, delta);
                }
                for (FadeInText score : scores) {
                    score.update(game, delta);
                }
            }
        }

        public void show(boolean b) {
            show = b;
            if (show) {
                readScores();
            }
        }

        public void readScores() {
            scores.clear();
            crowns.clear();
            String[][] scoress = reader.getScores(level.getCurrentLevel(), 5);
            int x = 240;
            int y = 210;
            int n = 0;
            for (String[] s : scoress) {
                if (s[2].equals("1")) {
                    crowns.add(new FadeInImage(resource.get("crown"), x, y + 3
                            + (25 * n), 32, 20, 500 + 400 * n));
                }
                scores.add(new FadeInText(s[0], master.jekyllMedium, x + 40, y
                        + (25 * n), 500 + 400 * n));
                scores.add(new FadeInText(s[1], master.jekyllMedium, x + 250, y
                        + (25 * n), 500 + 400 * n));
                n++;
            }
        }
    }

    private class StatGizmo implements Animatable {

        private StatMeter points, totalShrooms, blueShrooms, goldShrooms;
        private boolean show;

        public StatGizmo() throws SlickException {
            show = true;
            points = new StatMeter(master.jekyllMedium, 150, 120,
                    master.scorecard.getLevelScore(), MAX_LEVEL_SCORE);
            int mushrooms = (int) level.stats.getStat("level-mushrooms");
            int golden = (int) level.stats.getStat("level-golden");
            totalShrooms = new StatMeter(master.jekyllMedium, 150, 170,
                    mushrooms, MAX_LEVEL_COUNT);
            blueShrooms = new StatMeter(master.jekyllMedium, 150, 220,
                    mushrooms - golden, MAX_LEVEL_COUNT);
            goldShrooms = new StatMeter(master.jekyllMedium, 150, 270, golden,
                    MAX_GOLDEN_COUNT);
        }

        public void render(StateBasedGame game, Graphics g) {
            if (show) {
                statsIcons.getSprite(0, 0).draw(105, 118);
                points.render(game, g);
                statsIcons.getSprite(1, 0).draw(105, 168);
                totalShrooms.render(game, g);
                statsIcons.getSprite(2, 0).draw(105, 218);
                blueShrooms.render(game, g);
                statsIcons.getSprite(3, 0).draw(105, 268);
                goldShrooms.render(game, g);
                drawCentered(message);
            }
        }

        public void update(StateBasedGame game, int delta) {
            if (show) {
                points.update(game, delta);
                totalShrooms.update(game, delta);
                blueShrooms.update(game, delta);
                goldShrooms.update(game, delta);
            }
        }

        public void show(boolean b) {
            show = b;
        }

    }
}
