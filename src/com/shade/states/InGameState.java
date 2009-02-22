package com.shade.states;

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
import com.shade.controls.ControlSlice;
import com.shade.controls.ScoreControl;
import com.shade.controls.CounterControl;
import com.shade.controls.MeterControl;
import com.shade.controls.SerialStats;
import com.shade.controls.SlickButton;
import com.shade.controls.StatsControl;
import com.shade.controls.DayPhaseTimer.DayLightStatus;
import com.shade.levels.LevelManager;
import com.shade.levels.Model;
import com.shade.util.ResourceManager;

public class InGameState extends BasicGameState {

    public static final int ID = 3;

    private StateBasedGame game;
    private MasterState master;
    private ResourceManager resource;
    private int currentLevel, totalLevelsPlayed;
    private Model level;
    private LevelManager levels;
    private MeterControl meter;
    private CounterControl counter;
    private SlickButton play, back;

    public StatsControl stats;

    public InGameState(MasterState m) throws SlickException {
        levels = new LevelManager();
        master = m;
        resource = m.resource;
        resource.register("counter", "states/ingame/counter.png");
        resource.register("resume-up", "states/ingame/resume-up.png");
        resource.register("resume-down", "states/ingame/resume-down.png");
        resource.register("back-up", "states/common/back-up.png");
        resource.register("back-down", "states/common/back-down.png");
        initControls();
        initButtons();
    }

    @Override
    public int getID() {
        return ID;
    }

    /**
     * Play a game starting from the first level.
     */
    public void newGame() {
        currentLevel = 1;
        totalLevelsPlayed = 1;
        initLevel();
        resetControls();
        master.scorecard.reset();
        stats.reset();
    }

    public void newGame(int level) {
        currentLevel = level;
        totalLevelsPlayed = 1;
        initLevel();
        resetControls();
        master.scorecard.reset();
        stats.reset();
    }

    /**
     * Play a game starting from the level after the current one.
     */
    public void nextLevel() {
        currentLevel++;
        totalLevelsPlayed++;
        initLevel();
        resetControls();
        master.scorecard.reset();
    }

    /**
     * Play a game starting from the current level.
     */
    public void currentLevel() {
        initLevel();
        master.scorecard.rollbackLevel();
        resetControls();
    }

    private void initLevel() {
        if (currentLevel < levels.size()) {
            level = levels.get(currentLevel);
        }
    }

    /**
     * Requires level to be correctly set.
     */
    private void resetControls() {
    	meter.reset();
        counter.reset(level.getPar());
    }

    private void addBackControls() {
        master.control.add(counter);
        master.control.add(meter);
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("InGameState was init'd!");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        this.game = game;
        master.timer.reset();
        if (!master.dimmer.reversed()) {
            master.dimmer.rewind();
        }
        master.control.load(level);
        master.music.fade(MasterState.SECONDS_OF_DAYLIGHT, .1f, false);
        addBackControls();
    }

    private void safeExit(StateBasedGame game, int id) {
        master.control.flushControls();
        master.control.killPlayer();
        master.music.fade(2000, 1f, false);
        game.enterState(id);
    }

    public void exit(StateBasedGame game, int id) {
//    	System.out.println(meter.playerMetrics());
        recordMileage();
        recordDamage();
        recordMushroomsCollected();
        safeExit(game, id);
        if (parWasMet()) {
            String stat = "level-" + currentLevel + "-clear";
            SerialStats.write(stat, 1);
        }
    }

    private void recordDamage() {
        stats.replace("level-damage", meter.totalAmountLost());
        stats.replace("level-suntime", meter.totalTimeInSun());
    }

    private void recordMushroomsCollected() {
        stats.add("total-mushrooms", counter.totalCount);
        stats.replace("level-golden", counter.goldMushrooms);
        stats.replace("level-mushrooms", counter.totalCount);
        
        SerialStats.add("golden-mushrooms-collected", counter.goldMushrooms);
        SerialStats.add("mushrooms-collected", counter.totalCount);
        if (SerialStats.read("level-mushrooms-collected") < counter.totalCount) {
            SerialStats.write("level-mushrooms-collected", counter.totalCount);
        }
    }

    private void recordMileage() {
        stats.add("total-mileage", master.control.distanceTraveled());
        stats.replace("level-mileage", master.control.distanceTraveled());
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.scorecard.render(game, g);
        master.dimmer.render(game, g);
        if (container.isPaused()) {
            resource.get("header").draw(400, 0);
            play.render(game, g);
            back.render(game, g);
            drawCentered(container, "Paused (p)");
        }
        resource.get("trim").draw();
        
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 6; j++) {
//                g.drawRect(i * 100, j * 100, 100, 100);
//            }
//        }
    }

    private void drawCentered(GameContainer c, String s) {
        int x = (c.getWidth() - master.daisyLarge.getWidth(s)) / 2;
        int y = (c.getHeight() - master.daisyLarge.getHeight()) / 2;
        master.daisyLarge.drawString(x, y, s);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        if (currentLevel >= levels.size()) {
            safeExit(game, SelectState.ID);
            return;
        }
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

        if (isNight()) {
            // fade out
            exit(game, RecapState.ID);
        }
        
//        if (container.getInput().isKeyPressed(Input.KEY_R)) {
//            master.control.flushControls();
//            newGame(currentLevel);
//            enter(container, game);
//        }
    }

    public boolean parWasMet() {
        return counter.parWasMet();
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }

    private boolean isNight() {
        return master.timer.getDaylightStatus() == DayLightStatus.NIGHT;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_P || key == Input.KEY_ESCAPE) {
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

    private void initControls() throws SlickException {
        stats = new StatsControl();
        meter = new MeterControl(20, 480);
        meter.register(new ControlListener() {

            public void fire(ControlSlice c) {
                // The player lost
                exit(game, RecapState.ID);
            }

        });

        Image c = resource.get("counter");
        counter = new CounterControl(140, 520, c, master.jekyllLarge, master.jekyllMedium);

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
                safeExit(game, SelectState.ID);
            }

        });
    }
}
