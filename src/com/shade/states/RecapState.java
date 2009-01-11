package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SlickButton;
import com.shade.util.ResourceManager;

public class RecapState extends BasicGameState {
    
    private static final String PASS_TEXT = "Level Clear!!";
    private static final String FAIL_TEXT = "Level ... failed?!";

    public static final int ID = 9;

    private MasterState master;
    private InGameState level;
    private ResourceManager resource;
    private SlickButton next, replay, back;

    private boolean par;

    public RecapState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("nextlevel-up", "states/recap/nextlevel-up.png");
        resource.register("nextlevel-down", "states/recap/nextlevel-down.png");
        resource.register("replay-up", "states/recap/replay-up.png");
        resource.register("replay-down", "states/recap/replay-down.png");
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
        level = (InGameState) game.getState(InGameState.ID);
        par = level.parWasMet();
        initButtons();
        master.dimmer.rewind();
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
        master.jekyllLarge.drawString(120, 100, (par) ? PASS_TEXT : FAIL_TEXT);
        
        master.jekyllSmall.drawString(120, 150, "Points earned: " + master.scorecard.getLevelScore());
        master.jekyllSmall.drawString(120, 175, "Total points: " + master.scorecard.getScore());
        master.jekyllSmall.drawString(120, 200, "Mushrooms collected: " + (int) level.stats.getStat("level-mushrooms"));
        master.jekyllSmall.drawString(120, 225, "Distance traveled: " + levelMileage());
        master.jekyllSmall.drawString(120, 250, "Time in the sun: " + level.stats.getStat("level-suntime"));
        master.jekyllSmall.drawString(120, 275, "Tan: " + calculateTan());
        resource.get("trim").draw();
    }
    
    private String calculateTan() {
        float d = level.stats.getStat("level-mileage");
        float tan = d / (level.stats.getStat("level-damage") + 1);
        return tan + "";
    }

    public String totalMileage() {
        return Math.floor(level.stats.getStat("total-mileage") / 10) + " feet"; 
    }
    
    public String levelMileage() {
        return Math.floor(level.stats.getStat("level-mileage") / 10) + " feet";
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
    }

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
                ((InGameState) game.getState(InGameState.ID)).currentLevel();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
                master.dimmer.reset();
            }

        });
    }

    private void initNextButton() throws SlickException {
        next = new SlickButton(620, 110, resource.get("nextlevel-up"), resource
                .get("nextlevel-down"));
        next.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                ((InGameState) game.getState(InGameState.ID)).nextLevel();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
                master.dimmer.reset();
            }

        });
    }

    private void initBackButton() throws SlickException {
        int y = (par) ? 150 : 130;
        back = new SlickButton(620, y, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
                master.dimmer.reverse();
            }

        });
    }
}
