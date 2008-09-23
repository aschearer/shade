package com.shade.states;


import java.awt.Font;
import java.io.InputStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.crash.Grid;
import com.shade.entities.Basket;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Fence;
import com.shade.entities.Player;
import com.shade.shadows.ShadowCaster;
import com.shade.shadows.ShadowLevel;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Image backgroundSprite, trimSprite, counterSprite;
    private TrueTypeFont counterFont;

    private ShadowLevel level;
    private int sunTimer, totalTime;
    private float sunAngle;

    private ShadowCaster[] b;

    private Basket basket;

    private Player player;

    private Grid grid;

    
    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        initSprites();
        initFonts();
        sunAngle = 2.5f;
        grid = new Grid(8, 6, 100);
        level = new ShadowLevel(grid);
        
        /* TODO there's a rendering priority problem involved here... */
        basket = new Basket(400, 250, 65, 40);
        grid.add(basket);
        
        b = new ShadowCaster[16];
        // boxes
        b[0] = new Block(55, 355, 125, 125, 16);
        b[1] = new Block(224, 424, 56, 56, 6);
        b[2] = new Block(324, 424, 56, 56, 6);
        b[3] = new Block(75, 225, 56, 56, 6);
        b[4] = new Block(545, 380, 80, 80, 10);
        b[5] = new Block(445, 460, 80, 80, 10);
        // domes
        b[6] = new Dome(288, 165, 32, 7);
        b[7] = new Dome(180, 95, 44, 10);
        b[8] = new Dome(300, 85, 25, 6);
        b[9] = new Dome(680, 70, 28, 6);
        b[10] = new Dome(600, 120, 40, 9);
        b[11] = new Dome(680, 220, 60, 13);
        // fences
        b[12] = new Fence(225, 225, 11, 120, 6);
        b[13] = new Fence(390, 140, 120, 11, 6);
        b[14] = new Fence(715, 368, 11, 120, 6);
        b[15] = new Fence(50, 50, 11, 120, 6);
        
        for (int i = 0; i < b.length; i++) {
            level.add(b[i]);
        }
        
        grid.update();
        level.updateShadowscape(sunAngle);
        
        container.setSoundVolume(0f);
        // add five mushrooms to start
        for (int i = 0; i < 5; i++) {
            level.plant();
        }
        container.setSoundVolume(1f);
        
        player = new Player(400, 350, 18);
        level.add(player);
    }

    private void initFonts() throws SlickException {
        try {
            InputStream oi = ResourceLoader
                    .getResourceAsStream("states/ingame/jekyll.ttf");
            Font jekyll = Font.createFont(Font.TRUETYPE_FONT, oi);
            counterFont = new TrueTypeFont(jekyll.deriveFont(36f), true);;
        } catch (Exception e) {
            throw new SlickException("Failed to load font.", e);
        }   
    }

    private void initSprites() throws SlickException {
        backgroundSprite = new Image("states/ingame/background.png");
        trimSprite = new Image("states/ingame/trim.png");
        counterSprite = new Image("states/ingame/counter.png");
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        backgroundSprite.draw();
        basket.render(g);
        level.render(g);
        trimSprite.draw();
        counterSprite.draw(25, 510);
        counterFont.drawString(128, 518, player.mushroomsCollected + "");
//        grid.debugDraw(g);
    }

    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        level.update(game, delta);
        basket.update(game, delta);
        totalTime += delta;
        sunTimer += delta;
        
        double r = Math.random() * 1000;
        if (r < 3) {
            level.plant();
        }
        
        if (totalTime > 8000) {
            level.plant();
            totalTime = 0;
        }
        
//        if (sunTimer > 500) {
            sunTimer = 0;
            sunAngle += .0005f;
            level.updateShadowscape(sunAngle);
//        }
    }

}
