package com.shade.states;

import java.awt.Font;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.ScrollingText;
import com.shade.controls.SlickButton;
import com.shade.resource.ResourceManager;

public class CreditState extends BasicGameState {

    public static final int ID = 5;

    private static final String FEEDBACK_URL = "http://anotherearlymorning.com/";

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, feedback, back;
    private int timer;
    private Color screen;

    private int fadeTimer;

    private LinkedList<ScrollingText> credits;

    public CreditState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("feedback-up", "states/credits/feedback-up.png");
        resource.register("feedback-down", "states/credits/feedback-down.png");
        screen = new Color(Color.black);
        initCredits(loadFont());

    }

    private void initCredits(TrueTypeFont f) {
        credits = new LinkedList<ScrollingText>();
        Scanner s = new Scanner(ResourceLoader
                .getResourceAsStream("states/credits/credits.txt"));
        float y = 0;
        float d = f.getHeight() * 1.5f;
        while (s.hasNextLine()) {
            credits.add(new ScrollingText(s.nextLine(), f, 600 + y * d, 40));
            y++;
        }
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
        timer = 0;
        fadeTimer = 0;
        screen.a = 0f;
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        resource.get("header").draw(400, 0);
        play.render(game, g);
        feedback.render(game, g);
        back.render(game, g);
        g.setColor(screen);
        g.fillRect(0, 0, container.getWidth(), container.getHeight());
        g.setColor(Color.white);
        if (screen.a >= .6f && fadeTimer > 1000) {
            for (ScrollingText s : credits) {
                s.render(game, g);
            }
        }
        resource.get("trim").draw();
    }

    // render the aquarium
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        timer += delta;
        fadeTimer += delta;
        if (screen.a < .6f && fadeTimer > 100) {
            screen.a += .05f;
            fadeTimer = 0;
        }
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            feedback.update(game, delta);
            back.update(game, delta);
        }
        // see what I'm doing here, I'm co-opting the fadeTimer
        for (ScrollingText s : credits) {
            s.update(game, delta);            
            if (screen.a >= .6f && fadeTimer > 1000) {
                s.start();
            }
        }
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initMoreScoresButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("play-up"), resource
                .get("play-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initMoreScoresButton() throws SlickException {
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
            }

        });
    }

    private TrueTypeFont loadFont() throws SlickException {
        try {
            InputStream oi = ResourceLoader
                    .getResourceAsStream("states/common/daisymf.ttf");
            Font jekyll = Font.createFont(Font.TRUETYPE_FONT, oi);
            return new TrueTypeFont(jekyll.deriveFont(16f), true);
        } catch (Exception e) {
            throw new SlickException("Failed to load font.", e);
        }
    }
}
