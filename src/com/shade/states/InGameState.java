package com.shade.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.ControlListener;
import com.shade.controls.CounterControl;
import com.shade.controls.MeterControl;
import com.shade.controls.ScoreControl;
import com.shade.controls.SlickButton;
import com.shade.controls.DayPhaseTimer.DayLightStatus;
import com.shade.levels.LevelManager;
import com.shade.util.ResourceManager;
import com.shade.states.util.BirdCalls;
import com.shade.states.util.Dimmer;

public class InGameState extends BasicGameState {

    public static final int ID = 3;

    private static final float GAME_CLEAR_BONUS = 1000;

    private LevelManager manager;
    private MasterState master;
    private ResourceManager resource;
    private CounterControl counter;
    private MeterControl meter;
    private StateBasedGame game;
    private SlickButton play, back;
    private int nextLevel;
    private BirdCalls birds;
    private boolean transitioning;
    private Dimmer transition;
    private int transitionTimer;
    private Color levelText;

    public InGameState(MasterState m) throws SlickException {
        manager = new LevelManager();
        master = m;
        resource = m.resource;
        resource.register("counter", "states/ingame/counter.png");
        resource.register("resume-up", "states/ingame/resume-up.png");
        resource.register("resume-down", "states/ingame/resume-down.png");
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
        birds = new BirdCalls();
        transition = new Dimmer(1f);
        levelText = new Color(Color.white);
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
        master.dimmer.rewind();
        transitioning = false;
        master.control.add(counter);
        master.control.add(meter);
        master.control.load(manager.next());
        nextLevel = 2;
        levelText.a = 0;
    }

    // render the gameplay
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.scorecard.render(game, g);
        master.dimmer.render(game, g);
        transition.render(game, g);
        if (transitionTimer > 0) {
            fadeCentered(container, "Level " + nextLevel);
        }
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
        // if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
        // manager.rewind();
        // exit(game, TitleState.ID);
        // }
        // if (container.getInput().isKeyPressed(Input.KEY_R)) {
        // manager.rewind();
        // loadNextLevel(game);
        // }
        if (isNight() && !transitioning) {
            if (isLastLevel()) {                
                master.scorecard.add(GAME_CLEAR_BONUS);
                master.scorecard.setBeaten();
                master.music.fade(2000, 1f, false);
                exit(game, EnterScoreState.ID);
            } else {
                transition.reset();
                transitioning = true;
            }
        }
        if (!transitioning && master.music.getVolume() == 1) {
            master.music.fade(MasterState.SECONDS_OF_DAYLIGHT, .1f, false);
        }
        if (transitioning || transition.reversed()) {
            transition.update(game, delta);
        }
        if (transitioning && transition.finished()) {
            transitionTimer += delta;
        }
        if (transition.finished() && transitionTimer > 2000) {
            transitioning = false;
            transitionTimer = 0;
            transition.reverse();
            meter.awardBonus();
            master.timer.reset();
            master.music.fade(2000, 1f, false);
            loadNextLevel(game);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (!transitioning && key == Input.KEY_P) {
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

    private void fadeCentered(GameContainer c, String s) {
        int x = (c.getWidth() - master.daisyLarge.getWidth(s)) / 2;
        int y = (c.getHeight() - master.daisyLarge.getHeight()) / 2;
        master.daisyLarge.drawString(x, y, s, levelText);
        levelText.a += .05f;
    }

    private void exit(StateBasedGame game, int state) {
        master.control.flushControls();
        master.control.killPlayer();
        game.enterState(state);
    }

    private boolean isLastLevel() {
        return !manager.hasNext();
    }

    private void loadNextLevel(StateBasedGame game) {
        if (!isLastLevel()) {
            birds.play();
            levelText.a = 0;
            master.control.load(manager.next());
            nextLevel++;
        }
    }

    private boolean isNight() {
        return master.timer.getDaylightStatus() == DayLightStatus.NIGHT;
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
                master.music.resume();
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
