package com.shade.states;

import java.util.ArrayList;
import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.base.Animatable;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.InstructionImage;
import com.shade.controls.InstructionText;
import com.shade.controls.KeyListener;
import com.shade.controls.SlickButton;
import com.shade.controls.TwoToneButton;
import com.shade.levels.LevelManager;
import com.shade.levels.Model;
import com.shade.states.util.Dimmer;
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

    private int timer;

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
        if (master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        initFlowButtons();
        initButtons();
        initInstructions(master.daisyXLarge);
        timer = 0;
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        resource.get("backdrop").draw(0, 400);
        play.render(game, g);
        back.render(game, g);
        prev.render(game, g);
        next.render(game, g);
        renderInstructionStep();
        levels.render(game, g);
        resource.get("trim").draw();
    }

    private void renderInstructionStep() {
        master.jekyllXSmall.drawString(18, 495, levels.current() + " of "
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
            level.newGame(levels.current);
            if (levels.current == 0) {
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
                level.newGame(levels.current);
                if (levels.current == 0) {
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
                levels.next();
                master.control.load(manager.get(levels.current));
                master.control.killPlayer();
            }

        });

        next.register(Input.KEY_RIGHT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                levels.next();
                master.control.load(manager.get(levels.current));
                master.control.killPlayer();
            }

        });

        prev.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                levels.prev();
                master.control.load(manager.get(levels.current));
                master.control.killPlayer();

            }

        });

        prev.register(Input.KEY_LEFT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                levels.prev();
                master.control.load(manager.get(levels.current));
                master.control.killPlayer();
            }

        });

    }

    private void initInstructions(TrueTypeFont f) {
        levels = new LevelSet();
        int n = 0;
        while (n < 10) {
            String level = "Level " + (n + 1);
            float x = 400 - (f.getWidth(level) / 2);
            InstructionText t = new InstructionText(x, 435, level, f);
            t.setTimer(LEVEL_STATE_DELAY + n * LEVEL_BUFFER);
            levels.add(t);
            n++;
        }
    }

    private class LevelSet implements Animatable {

        private boolean finished;
        private int current;
        private ArrayList<InstructionText> text;

        public LevelSet() {
            text = new ArrayList<InstructionText>();
        }

        public int current() {
            return current + 1;
        }

        public int size() {
            return text.size();
        }

        public void add(InstructionText t) {
            if (text.size() == 0) {
                t.activate();
            }
            text.add(t);
        }

        public void next() {
            text.get(current).deactivate();
            if (current < text.size()) {
                current++;
                text.get(current).activate();
                if (current == text.size() - 1) {
                    finished = true;
                }
            }
        }

        public void prev() {
            text.get(current).deactivate();
            current--;
            text.get(current).activate();
            if (finished) {
                finished = false;
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            for (InstructionText s : text) {
                s.render(game, g);
            }
        }

        public void update(StateBasedGame game, int delta) {
            for (InstructionText s : text) {
                s.update(game, delta);
            }
        }

        public boolean started() {
            return current == 0;
        }

        public boolean finished() {
            return finished;
        }

    }
}
