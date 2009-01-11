package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.SlickButton;
import com.shade.util.ResourceManager;

public class SelectState extends BasicGameState {

    public static final int ID = 10;

    private MasterState master;
    private InGameState level;
    private ResourceManager resource;

    private SlickButton[] options;
    private SlickButton play, back;
    
    private int timer;

    @Override
    public int getID() {
        return ID;
    }

    public SelectState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        
        resource.register("newgame-up", "states/select/newgame-up.png");
        resource.register("newgame-down", "states/select/newgame-down.png");
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("SelectState was init'd!");
    }

    public void enter(GameContainer container, final StateBasedGame game)
            throws SlickException {
        level = (InGameState) game.getState(InGameState.ID);
        master.dimmer.reset();
        initOptions();
        initButtons();
        timer = 0;
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        resource.get("trim").draw();
        for (int i = 0; i < options.length; i++) {
            options[i].render(game, g);
        }
        play.render(game, g);
        back.render(game, g);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        for (int i = 0; i < options.length; i++) {
            options[i].update(game, delta);
        }
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            back.update(game, delta);
        }
    }
    
    private void initOptions() throws SlickException {
        options = new SlickButton[12];
        SpriteSheet thumbnails = new SpriteSheet("states/select/thumbnails.png", 97, 62);
        Image t = thumbnails.getSprite(0, 0);
        options[0] = new SlickButton(70, 90, t, t);
        options[0].addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                level.newGame();
                game.enterState(InstructionState.ID);
            }
            
        });
        
        Image locked = new Image("states/select/locked.png");
        for (int i = 1; i < 12; i++) {
            int x = i % 3;
            int y = i / 3;
            if (master.levelsLock.isUnlocked(i)) {
                t = thumbnails.getSprite(x, y);
                options[i] = new SlickButton(70 + x * 120, 90 + y * 90, t, t);
                options[i].setUserData(i);
                options[i].addListener(new ClickListener() {

                    public void onClick(StateBasedGame game, Button clicked) {
                        level.newGame((Integer) ((SlickButton)clicked).getUserData());
                        game.enterState(InGameState.ID, new FadeOutTransition(), null);
                    }
                    
                });
            } else {
                options[i] = new SlickButton(70 + x * 120, 90 + y * 90, locked, locked);
                options[i].addListener(new ClickListener() {

                    public void onClick(StateBasedGame game, Button clicked) {
                        // do nothing
                    }
                    
                });
            }
        }
    }
    
    private void initButtons() throws SlickException {
        initPlayButton();
        initBackButton();
    }
    
    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("newgame-up"), resource
                .get("newgame-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                level.newGame();
                game.enterState(InstructionState.ID);
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
}
