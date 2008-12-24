package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.ControlListener;
import com.shade.controls.CounterControl;
import com.shade.controls.MeterControl;
import com.shade.controls.ScoreControl;
import com.shade.controls.SlickButton;
import com.shade.controls.DayPhaseTimer.DayLightStatus;
import com.shade.levels.LevelManager;
import com.shade.resource.ResourceManager;

public class InGameState extends BasicGameState {

    public static final int ID = 3;

    private LevelManager manager;
    private MasterState master;
    private ResourceManager resource;
    private CounterControl counter;
    private MeterControl meter;
    private int timer;
    private boolean transitioning;
    private Transition transition;
    private StateBasedGame game;
    private SlickButton play, back;

    public InGameState(MasterState m) throws SlickException {
        manager = new LevelManager(8, 6, 100);
        master = m;
        resource = m.resource;
        resource.register("counter", "states/ingame/counter.png");
        resource.register("resume-up", "states/ingame/resume-up.png");
        resource.register("resume-down", "states/ingame/resume-down.png");
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
        transition = new FadeOutTransition();
        initControls();
        initButtons();
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("InGameState was init'd!");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        this.game = game;
        counter.reset();
        meter.reset();
        master.scorecard.reset();
        master.timer.reset();
        manager.rewind();
        master.control.add(counter);
        master.control.add(meter);
        master.control.load(manager.next());
        timer = 0;
        transitioning = false;
        master.dimmer.rewind();
    }

    // render the gameplay
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.scorecard.render(game, g);
        if (transitioning) {
            transition.postRender(game, container, g);
        }
        master.dimmer.render(game, g);
        if (container.isPaused()) {
            resource.get("header").draw(400, 0);
            play.render(game, g);
            back.render(game, g);
            drawCentered(container, "Paused (p)");
        }
        resource.get("trim").draw();
    }

    // render the gameplay
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        if (container.isPaused()) {
            master.dimmer.update(game, 25);
            play.update(game, 25);
            back.update(game, 25);
            return;
        }
        if (master.dimmer.reversed()) {
            master.dimmer.update(game, delta);
        }
        master.control.update(game, delta);
        master.scorecard.update(game, delta);
        if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            manager.rewind();
            exit(game, TitleState.ID);
        }
        // if (container.getInput().isKeyPressed(Input.KEY_R)) {
        // manager.rewind();
        // loadNextLevel(game);
        // }
        timer += delta;
        if (master.timer.getDaylightStatus() == DayLightStatus.NIGHT) {
            transitioning = true;
        }
        if (transitioning) {
            transition.update(game, container, delta);
            if (transition.isComplete()) {
                transitioning = false;
                timer = 0;
                meter.awardBonus();
                master.timer.reset();
                master.dimmer.fastforward();
                loadNextLevel(game);
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_P) {
            if (game.getContainer().isPaused()) {
                game.getContainer().resume();
                master.music.resume();
                master.dimmer.reverse();
            } else {
                game.getContainer().pause();
                master.music.pause();
                master.dimmer.reset();
            }
        }
    }

    private void drawCentered(GameContainer c, String s) {
        int x = (c.getWidth() - master.daisyLarge.getWidth(s)) / 2;
        int y = (c.getHeight() - master.daisyLarge.getHeight()) / 2;
        master.daisyLarge.drawString(x, y, s);
    }

    private void exit(StateBasedGame game, int state) {
        master.control.flushControls();
        master.control.killPlayer();
        game.enterState(state);
    }

    private void loadNextLevel(StateBasedGame game) {
        if (manager.hasNext()) {
            master.control.load(manager.next());
        } else {
            exit(game, EnterScoreState.ID);
        }
    }

    private void initControls() throws SlickException {
        meter = new MeterControl(20, 480);
        meter.register(new ControlListener() {

            public void fire() {
                // The player lost
                exit(game, EnterScoreState.ID);
            }

        });

        Image c = resource.get("counter");
        counter = new CounterControl(140, 520, c, master.jekyllLarge);

        master.scorecard = new ScoreControl(10, 10, master.jekyllLarge);
        meter.pass(master.scorecard);
        counter.register(master.scorecard);
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("resume-up"), resource
                .get("resume-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.getContainer().resume();
                master.dimmer.reverse();
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 130, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.getContainer().resume();
                master.music.resume();
                master.dimmer.reverse();
                manager.rewind();
                exit(game, TitleState.ID);
            }

        });
    }
}
