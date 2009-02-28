package com.shade.states;

import java.util.ArrayList;
import java.util.Scanner;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.base.Animatable;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.InstructionImage;
import com.shade.controls.InstructionText;
import com.shade.controls.KeyListener;
import com.shade.controls.SerialStats;
import com.shade.controls.SlickButton;
import com.shade.controls.TwoToneButton;
import com.shade.levels.LevelManager;
import com.shade.util.ResourceManager;

public class SelectState extends BasicGameState {

    public static final int ID = 10;

    private static final int LEVEL_STATE_DELAY = 600;
    private static final int LEVEL_BUFFER = 5000;

    private MasterState master;
    private InGameState level;
    private LevelManager manager;
    private ResourceManager resource;

    private SlickButton play, back;
    private TwoToneButton next, prev;
    private LevelSet levels;
    int currentLevel;

    private int timer;
    private Sound click;

    private StateBasedGame game;

    @Override
    public int getID() {
        return ID;
    }

    public SelectState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;

        resource.register("newgame-up", "states/select/newgame-up.png");
        resource.register("newgame-down", "states/select/newgame-down.png");
        resource.register("question-mark", "states/select/question-mark.png");
        resource.register("crown", "states/highscore/crown.png");

        click = new Sound("states/common/click.ogg");

        manager = new LevelManager();
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("SelectState was init'd!");
    }

    public void enter(GameContainer container, final StateBasedGame game)
            throws SlickException {
        this.game = game;
        level = (InGameState) game.getState(InGameState.ID);
        currentLevel = level.getCurrentLevel();
        initLevels(master.jekyllMedium);
        if (master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        initFlowButtons();
        initButtons();
        timer = 0;
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        g.setColor(Color.black);
        if (!master.levelsLock.isUnlocked(currentLevel)) {
            g.fillRect(0, 0, 800, 600);
        }
        g.setColor(Color.white);
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        resource.get("backdrop").draw(0, 400);
        play.render(game, g);
        back.render(game, g);
        prev.render(game, g);
        next.render(game, g);
        renderInstructionStep();
        if (!master.levelsLock.isUnlocked(currentLevel)) {
            resource.get("question-mark").draw(310, 160);
        }
        if (currentLevel > 0 && SerialStats.read("level-" + currentLevel + "-clear") == 1) {
            resource.get("crown").draw(15, 412);
        }
        levels.render(game, g);
        resource.get("trim").draw();
    }

    private void renderInstructionStep() {
        master.jekyllXSmall.drawString(18, 495, (1 + currentLevel) + " of "
                + levels.size());
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        resource.get("backdrop").draw(0, 400);
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            back.update(game, delta);
            prev.update(game, delta);
            next.update(game, delta);
        }
        prev.active(levels.started());
        next.active(levels.finished());
        levels.update(game, delta);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_ENTER) {
            click.play();
            if (!master.levelsLock.isUnlocked(currentLevel)) {
                return;
            }
            level.newGame(currentLevel);
            if (currentLevel == 0) {
                game.enterState(InstructionState.ID);
            } else {
                game.enterState(InGameState.ID);
            }
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("play-up"), resource
                .get("play-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                if (!master.levelsLock.isUnlocked(currentLevel)) {
                    return;
                }
                level.newGame(currentLevel);
                if (currentLevel == 0) {
                    game.enterState(InstructionState.ID);
                } else {
                    game.enterState(InGameState.ID);
                }
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 130, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
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
                currentLevel++;
                levels.next();
                master.control.load(manager.get(currentLevel));
                master.control.killPlayer();
            }

        });

        next.register(Input.KEY_RIGHT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                currentLevel++;
                levels.next();
                master.control.load(manager.get(currentLevel));
                master.control.killPlayer();
            }

        });

        prev.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                currentLevel--;
                levels.prev();
                master.control.load(manager.get(currentLevel));
                master.control.killPlayer();

            }

        });

        prev.register(Input.KEY_LEFT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                currentLevel--;
                levels.prev();
                master.control.load(manager.get(currentLevel));
                master.control.killPlayer();
            }

        });

    }

    private void initLevels(TrueTypeFont f) throws SlickException {
        Scanner hints = new Scanner(ResourceLoader
                .getResourceAsStream("states/select/hints.txt"));
        Image locked = new Image("states/select/locked.png");
        Scanner names = new Scanner(ResourceLoader
                .getResourceAsStream("states/select/levels.txt"));
        Image unlocked = new Image("states/select/unlocked.png");
        
        levels = new LevelSet();
        float x = 90;
        int n = 0;
        while (hints.hasNextLine()) {
            InstructionImage i = null;
            InstructionText t = null;
            if (master.levelsLock.isUnlocked(n)) {
                i = new InstructionImage(x, 425, unlocked);
                i.setTimer(LEVEL_STATE_DELAY + n * LEVEL_BUFFER);
                t = new InstructionText(x + 100, 445, names.nextLine(), f);
                t.setTimer(LEVEL_STATE_DELAY + n * LEVEL_BUFFER);
                hints.nextLine();
            } else {
                i = new InstructionImage(x - 6, 425 + 11, locked);
                i.setTimer(LEVEL_STATE_DELAY + n * LEVEL_BUFFER);
                t = new InstructionText(x + 100, 445, hints.nextLine(), f);
                t.setTimer(LEVEL_STATE_DELAY + n * LEVEL_BUFFER);
                names.nextLine();
            }
            levels.add(i, t);
            n++;
        }
        
        

    }

    private class LevelSet implements Animatable {

        private boolean finished;
        private ArrayList<InstructionImage> images;
        private ArrayList<InstructionText> text;

        public LevelSet() {
            images = new ArrayList<InstructionImage>();
            text = new ArrayList<InstructionText>();
            finished = (currentLevel == LevelManager.NUM_LEVELS - 1);
        }

        public void moveTo(int level) {
            currentLevel = level;
            for (int i = 0; i < text.size(); i++) {
                if (i == level) {
                    images.get(i).activate();
                    text.get(i).activate();
                } else {
                    images.get(i).deactivate();
                    text.get(i).deactivate();
                }
            }
        }

        public int current() {
            return currentLevel + 1;
        }

        public int size() {
            return text.size();
        }

        public void add(InstructionImage i, InstructionText t) {
            if (text.size() == currentLevel) {
                t.activate();
                i.activate();
            }
            images.add(i);
            text.add(t);
        }

        public void next() {
            int last = currentLevel - 1;
            text.get(last).deactivate();
            images.get(last).deactivate();
            if (currentLevel < text.size()) {
                text.get(currentLevel).activate();
                images.get(currentLevel).activate();
                if (currentLevel == text.size() - 1) {
                    finished = true;
                }
            }
        }

        public void prev() {
            int last = currentLevel + 1;
            text.get(last).deactivate();
            images.get(last).deactivate();
            text.get(currentLevel).activate();
            images.get(currentLevel).activate();
            if (finished) {
                finished = false;
            }
        }

        public void reset(int last) {
            images.get(last).reset();
            text.get(last).reset();
            images.get(currentLevel).activate();
            text.get(currentLevel).activate();
        }

        public void render(StateBasedGame game, Graphics g) {
            for (InstructionText s : text) {
                s.render(game, g);
            }
            for (InstructionImage i : images) {
                i.render(game, g);
            }
        }

        public void update(StateBasedGame game, int delta) {
            for (InstructionText s : text) {
                s.update(game, delta);
            }
            for (InstructionImage i : images) {
                i.update(game, delta);
            }
        }

        public boolean started() {
            return currentLevel == 0;
        }

        public boolean finished() {
            return finished;
        }

    }
}
