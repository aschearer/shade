package com.shade.states;

import java.awt.Font;
import java.io.InputStream;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.*;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.controls.*;
import com.shade.crash.*;
import com.shade.entities.*;
import com.shade.shadows.*;

public class InGameState extends BasicGameState {

    public static final int ID = 1;
    public static final float TRANSITION_TIME = 1f / 7;
    public static final float MAX_SHADOW = 0.6f;
    public static final float SUN_ANGLE_INCREMENT = 0.005f;
    public static final int SECONDS_PER_DAY = (int) Math.ceil(Math.PI * 32
            / SUN_ANGLE_INCREMENT);

    private enum Status {
        NOT_STARTED, RUNNING, PAUSED, GAME_OVER
    };

    private Status currentStatus;

    private ShadowLevel level;
    private MeterControl meter;
    private CounterControl counter;

    private Image backgroundSprite, trimSprite;
    private Image counterSprite;
    private TrueTypeFont counterFont;

    private float sunAngle;

    private Player player;

    private int timer, totalTimer;

    private int numMoles;

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        level = new ShadowLevel(new Grid(8, 6, 100));
        sunAngle = 2.5f;
        currentStatus = Status.NOT_STARTED;
        initSprites();
        initFonts();
    }

    private void initFonts() throws SlickException {
        try {
            InputStream oi = ResourceLoader
                    .getResourceAsStream("states/ingame/jekyll.ttf");
            Font jekyll = Font.createFont(Font.TRUETYPE_FONT, oi);
            counterFont = new TrueTypeFont(jekyll.deriveFont(36f), true);
        } catch (Exception e) {
            throw new SlickException("Failed to load font.", e);
        }
    }

    private void initSprites() throws SlickException {
        backgroundSprite = new Image("states/ingame/background.png");
        trimSprite = new Image("states/ingame/trim.png");
        counterSprite = new Image("states/ingame/counter.png");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        currentStatus = Status.RUNNING;
        totalTimer = 0;
        timer = 0;

        level.clear();
        level.updateShadowscape(sunAngle, (float) Math.sin(sunAngle) * 2f);
        meter = new MeterControl(20, 456, 100, 100);
        counter = new CounterControl(60, 520, counterSprite, counterFont);
        numMoles = 0;

        initObstacles();
        initBasket();
        initPlayer();
    }

    private void initShrooms(GameContainer container) {
        for (int i = 0; i < 5; i++) {
            level.plant();
        }
    }

    private void initObstacles() throws SlickException {
        LinkedList<ShadowCaster> casters = new LinkedList<ShadowCaster>();
        casters.add(new Block(55, 355, 125, 125, 16));
        casters.add(new Block(224, 424, 56, 56, 6));
        casters.add(new Block(324, 424, 56, 56, 6));
        casters.add(new Block(75, 225, 56, 56, 6));
        casters.add(new Block(545, 330, 80, 80, 10));
        casters.add(new Block(445, 460, 80, 80, 10));
        // domes
        casters.add(new Dome(288, 165, 32, 7));
        casters.add(new Dome(180, 95, 44, 10));
        casters.add(new Dome(300, 65, 25, 6));
        casters.add(new Dome(710, 80, 28, 6));
        casters.add(new Dome(600, 100, 40, 9));
        casters.add(new Dome(680, 220, 60, 13));
        // fences
        casters.add(new Fence(225, 225, 11, 120, 5));
        casters.add(new Fence(390, 140, 120, 11, 5));
        casters.add(new Fence(715, 368, 11, 120, 5));
        casters.add(new Fence(50, 50, 11, 120, 5));

        for (ShadowCaster c : casters) {
            level.add(c);
        }
    }

    private void initBasket() throws SlickException {
        Basket b = new Basket(400, 250, 65, 40);
        level.add(b);
    }

    private void initPlayer() throws SlickException {
        player = new Player(400, 350, 18);
        player.add(counter);
        player.add(meter);

        level.add(player);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        backgroundSprite.draw();
        level.render(game, g);
        trimSprite.draw();
        meter.render(game, g);
        counter.render(game, g);
        
		int timeofday = timer%SECONDS_PER_DAY;
		// is it day or night?
		if(timeofday>1.0*SECONDS_PER_DAY*(1f/2-TRANSITION_TIME)){
			float factor = MAX_SHADOW;
			float colorizer = 0;
			float colorizeg = 0;
			float colorizeb = 0;
			if(timeofday<1.0*SECONDS_PER_DAY/2){
				factor = (float)1.0*MAX_SHADOW*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1);
				colorizer = 0.2f*(float)Math.abs(Math.sin(Math.PI*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1)));
				colorizeg = 0.1f*(float)Math.abs(Math.sin(Math.PI*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1)));
				
			}
			if(timeofday>1.0*SECONDS_PER_DAY*(1-TRANSITION_TIME)){
				factor = MAX_SHADOW*(SECONDS_PER_DAY-timeofday)/(SECONDS_PER_DAY*TRANSITION_TIME);
				colorizer = 0.1f*(float)Math.abs(Math.cos(Math.PI/2*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1)));
				colorizeg = 0.1f*(float)Math.abs(Math.cos(Math.PI/2*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1)));
				colorizeb = 0.05f*(float)Math.abs(Math.cos(Math.PI/2*((timeofday-SECONDS_PER_DAY/2f)/(SECONDS_PER_DAY*TRANSITION_TIME)+1)));
			}
			Color night = new Color(colorizer,colorizeg,colorizeb,factor);
			
			g.setColor(night);
			g.fillRect(0, 0, game.getContainer().getScreenWidth(), game.getContainer().getScreenHeight());
			g.setColor(Color.white);
		}
        meter.render(game, g);
        counter.render(game, g);
        if (currentStatus == Status.GAME_OVER) {
            counterFont.drawString(320, 300, "Game Over");
        }
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        updateShadow();

        if (currentStatus == Status.RUNNING) {
            level.update(game, delta);
            timer += delta;
            totalTimer += delta;
            
            // first run
            if (totalTimer == delta) {
                initShrooms(container);
            }

            // Randomly plant mushrooms
            if (Math.random() > .9965 || timer % 5000 + delta > 5000) {
                level.plant();
            }

            if (counter.value > 5 && numMoles < 1) {
                level.add(new Mole(4000));
                numMoles++;
            }
            if (counter.value > 25 && numMoles < 2) {
                level.add(new Mole(5000));
                numMoles++;
            }
            if (counter.value > 50 && numMoles < 3) {
                level.add(new Mole(6000));
                numMoles++;
            }

            meter.update(game, delta);
            counter.update(game, delta);

            if (!player.shaded) {
                meter.decrement();
            }

            // Check for lose condition
            if (meter.isEmpty()) {
                currentStatus = Status.GAME_OVER;
            }
        }

        // check whether to restart
        if (container.getInput().isKeyPressed(Input.KEY_R)) {
            enter(container, game);
        }
    }

    private void updateShadow() {
        sunAngle += SUN_ANGLE_INCREMENT;
        level.updateShadowscape(sunAngle, 10f);// (float)(10f/(1+0.9*Math.cos(sunAngle*2))));
    }

}
