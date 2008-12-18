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

import com.shade.controls.ControlListener;
import com.shade.controls.CounterControl;
import com.shade.controls.MeterControl;
import com.shade.controls.ScoreControl;
import com.shade.levels.LevelManager;
import com.shade.resource.ResourceManager;
import com.shade.states.util.Dimmer;

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
    private Dimmer dimmer;


    public InGameState(MasterState m) throws SlickException {
        manager = new LevelManager(8, 6, 100);
        master = m;
        resource = m.resource;
        resource.register("counter", "states/ingame/counter.png");
        transition = new FadeOutTransition();
        dimmer = new Dimmer(.6f);
        initControls();
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
        manager.rewind();
        master.control.add(counter);
        master.control.add(meter);
        master.control.load(manager.next());
        timer = 0;
        transitioning = false;
    }

    // render the gameplay
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.scorecard.render(game, g);
        if (transitioning) {
            transition.postRender(game, container, g);
        }
        if (container.isPaused()) {
            dimmer.render(game, g);
            drawCentered(container, "Paused (p)", 320);
        }
        resource.get("trim").draw();
    }

    // render the gameplay
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        if (container.isPaused()) {
            dimmer.update(game, 15);
            return;
        }
        master.control.update(game, delta);
        master.scorecard.update(game, delta);
        if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            manager.rewind();
            exit(game, TitleState.ID);
        }
        timer += delta;
        if (timer > MasterState.SECONDS_PER_DAY / 2) {
            transitioning = true;
        }
        if (transitioning) {
            transition.update(game, container, delta);
            if (transition.isComplete()) {
                transitioning = false;
                timer = 0;
                meter.awardBonus();
                loadNextLevel(game);
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_P) {
            if (game.getContainer().isPaused()) {
                game.getContainer().resume();
            } else {
                game.getContainer().pause();
                dimmer.reset();
            }
        }
    }
    
    private void drawCentered(GameContainer c, String s, int y) {
        int x = (c.getWidth() - master.daisyMedium.getWidth(s)) / 2;
        master.daisyMedium.drawString(x, y, s);
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
        meter = new MeterControl(20, 456);
        meter.register(new ControlListener() {

            public void fire() {
                // The player lost
                exit(game, EnterScoreState.ID);
            }
            
        });
        
        Image c = resource.get("counter");
        counter = new CounterControl(60, 520, c, master.jekyllLarge);

        master.scorecard = new ScoreControl(10, 10, master.jekyllLarge);
        meter.pass(master.scorecard);
        counter.register(master.scorecard);
    }
}
