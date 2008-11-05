package com.shade.states;

import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.*;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.base.Entity;
import com.shade.controls.*;
import com.shade.crash.*;
import com.shade.entities.*;
import com.shade.entities.util.MushroomFactory;
import com.shade.lightrender.GlobalLight;
import com.shade.lightrender.GroundLight;
import com.shade.lightrender.InfiniteLight;
import com.shade.lightrender.LightMask;
import com.shade.lightrender.LightSource;
import com.shade.shadows.*;
import com.shade.shadows.ShadowEntity.ShadowIntensity;

public class InGameState extends BasicGameState {

    public static final int ID = 1;
    private static final float SUN_START_ANGLE = 2.5f;
    private static final float SUN_START_DEPTH = 10f;
    private static final float SUN_ANGLE_INCREMENT = 0.001f;

    private enum Status {
        NOT_STARTED, RUNNING, PAUSED, GAME_OVER
    };

    private Status currentStatus;

    private ShadowLevel level;
    private MeterControl meter;
    private CounterControl counter;
    private InGameControl control;

    private Image backgroundSprite, trimSprite;
    private Image counterSprite;
    private TrueTypeFont counterFont;

    private Player player;

    private int timer, totalTimer;
    
    LightMask l;
    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        level = new ShadowLevel(new Grid(8, 6, 200), SUN_START_ANGLE,
                SUN_START_DEPTH, SUN_ANGLE_INCREMENT);
        currentStatus = Status.NOT_STARTED;
        l = new LightMask(container.getWidth(),container.getHeight());
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
        l.add(backgroundSprite);
        trimSprite = new Image("states/ingame/trim.png");
        l.add(trimSprite);
        counterSprite = new Image("states/ingame/counter.png");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        currentStatus = Status.RUNNING;
        totalTimer = 0;
        timer = 0;

        level.clear();
        meter = new MeterControl(20, 456, 100, 100);
        counter = new CounterControl(60, 520, counterSprite, counterFont);

        initObstacles();
        initBasket();
        initPlayer();
        ArrayList<Monster> monmon = new ArrayList<Monster>();
        monmon.add(new Monster(450, 80));
        monmon.add(new Monster(650, 520));
        monmon.add(new Monster(200, 275));
        
        for(Monster mom:monmon){
        	l.add(mom);
        	level.add(mom);
        }
        
        control = new InGameControl(level, counter, meter, player);
    }

    private void initShrooms(GameContainer container) throws SlickException {
        for (int i = 0; i < 5; i++) {
            Vector2f p = level.randomPoint(container);
            level.add(MushroomFactory.makeMushroom(p.x, p.y));
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
        casters.add(new Fence(250, 250, 11, 120, 5));
        casters.add(new Fence(390, 140, 120, 11, 5));
        casters.add(new Fence(715, 368, 11, 120, 5));
        casters.add(new Fence(50, 50, 11, 120, 5));
        // shrubs
        //casters.add(new Shrub(300, 300));
        
        //gargoyles
        Gargoyle g1 = new Gargoyle(500,300);
        Gargoyle g2 = new Gargoyle(500,200);
        Gargoyle g3 = new Gargoyle(500,400);
    	casters.add(g1);
    	casters.add(g2);
    	casters.add(g3);
    	level.add((Entity)g1);
    	level.add((Entity)g2);
    	level.add((Entity)g3);
    	
    	GlobalLight lix = new GlobalLight(1500,0,1.0f,9000,10);
    	l.add(lix);


    	///*
    	LightSource light = new InfiniteLight(110,110,0.2f,100,3);
    	//l.add(light);
    	LightSource light2 = new InfiniteLight(400,300,0.1f,400,20);
    	//l.add(light2);
    	LightSource lightblock = new GroundLight(500,300,20,1.0f,250);
    	l.add(lightblock);
    	level.add((ShadowEntity)lightblock);
    	LightSource lightblock2 = new GroundLight(300,300,20,0.7f,100);
    	l.add(lightblock2);
    	level.add((ShadowEntity)lightblock2);
    	LightSource lightblock3 = new GroundLight(300,200,20,0.5f,40);
    	l.add(lightblock3);
    	level.add((ShadowEntity)lightblock3);
    	LightSource lightblock4 = new GroundLight(300,400,20,0.3f,40);
    	//l.add(lightblock4);
    	//level.add((ShadowEntity)lightblock4);
    	//*/
    	
        for (ShadowCaster c : casters) {
            level.add(c);
            l.add(c);
        }
    }

    private void initBasket() throws SlickException {
        Basket b = new Basket(400, 250, 65, 40);
        b.add(counter);
        b.add(meter);
        level.add(b);
        l.add(b);
    }

    private void initPlayer() throws SlickException {
        player = new Player(400, 350);
        level.add(player);
        l.add(player);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
    	//g.setDrawMode(Graphics.MODE_ADD);
		l.renderLights(g);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
		g.setColor(new Color(0,0,0,0.15f));
		//g.fill(new Rectangle(0, 0, 800, 600));
		GL11.glBlendFunc(GL11.GL_DST_ALPHA,GL11.GL_ONE);
		backgroundSprite.draw();
		l.renderEntities(game, g);
		l.renderCasters(game, g);
        level.render(game, g);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        meter.render(game, g);
        trimSprite.draw();
        counter.render(game, g);
        if (currentStatus == Status.GAME_OVER) {
            counterFont.drawString(320, 300, "Game Over");
        }
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
    	l.update(game, delta);
        if (currentStatus == Status.RUNNING) {
            level.update(game, delta);
            timer += delta;
            totalTimer += delta;

            // first run
            if (totalTimer == delta) {
                initShrooms(container);
            }
            
            meter.update(game, delta);
            counter.update(game, delta);
            control.update(game, delta);

            
            if(player.isStunned()){
            	meter.decrement(0.5);
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

}
