package com.shade.states;

import java.util.LinkedList;
import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.ScrollingText;
import com.shade.controls.SlickButton;
import com.shade.util.ResourceManager;

public class CreditState extends BasicGameState {

    private static final int CREDIT_DELAY = 1000;

    public static final int ID = 5;

    private static final String FEEDBACK_URL = "http://anotherearlymorning.com/shade/feedback";

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, feedback, back;
    private int timer;

    private LinkedList<ScrollingText> credits;

    public CreditState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("feedback-up", "states/credits/feedback-up.png");
        resource.register("feedback-down", "states/credits/feedback-down.png");
        initCredits(master.daisySmall);

    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("CreditState was init'd!");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        initButtons();
        initCredits(master.daisySmall);
        timer = 0;
        master.dimmer.reset();
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        if (timer > CREDIT_DELAY) {
            for (ScrollingText s : credits) {
                s.render(game, g);
            }
        }
        resource.get("header").draw(400, 0);
        play.render(game, g);
        feedback.render(game, g);
        back.render(game, g);
        resource.get("trim").draw();
    }

    // render the aquarium
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            feedback.update(game, delta);
            back.update(game, delta);
        }
        
        for (ScrollingText s : credits) {
            s.update(game, delta);            
            if (timer > CREDIT_DELAY) {
                s.start();
            }
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initFeedbackButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("play-up"), resource
                .get("play-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
            }

        });
    }

    private void initFeedbackButton() throws SlickException {
        feedback = new SlickButton(620, 130, resource.get("feedback-up"),
                resource.get("feedback-down"));
        feedback.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                BareBonesBrowserLaunch.openURL(FEEDBACK_URL);
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 150, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
                master.dimmer.reverse();
            }

        });
    }

    private void initCredits(TrueTypeFont f) {
        credits = new LinkedList<ScrollingText>();
        Scanner s = new Scanner(ResourceLoader
                .getResourceAsStream("states/credits/credits.txt"));
        float y = 0;
        float d = f.getHeight() * 1.5f;
        while (s.hasNextLine()) {
            String[] credit = s.nextLine().split(",");
            credits.add(new ScrollingText(credit[0], f, 40, 600 + y * d));
            if (credit.length > 1) {
                credits.add(new ScrollingText(credit[1], f, 300, 600 + y * d));
            }
            y++;
        }
    }
    
}
