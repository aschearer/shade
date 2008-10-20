package com.shade.controls;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.entities.Mushroom;
import com.shade.entities.Mushroom.MushroomType;

public class MeterControl implements MushroomCounter, Animatable {

    private static final float MIN_SCORE = 1;
    private static final float SCORE_INCREMENT = .3f;
    private static final float SCORE_MULTIPLE = 4;
    private static final float BASE_INCREMENT = .1f;
    private static final float WIDTH = 24;
    private static final float HEIGHT = 124;
    private static final Color BORDER = new Color(163, 183, 139);
    private static final Color ON = new Color(99, 125, 88);
    private static final Color OFF = new Color(163, 191, 95);

    private float x, y;
    private float value, max;
    private float score;

    public MeterControl(float x, float y, float value, float max) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.max = max;
        score = SCORE_MULTIPLE;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isFull() {
        return value == max;
    }

    public void onCollect(Mushroom shroomie) {
        valueMushroom(shroomie);
    }

    private void valueMushroom(Mushroom shroomie) {
    	/*for(MushroomType a : MushroomType.values()){
    		if(shroomie.type == a) 
    	}*/
        if (shroomie.type == MushroomType.POISON) {
            value -= shroomie.getSize() * score;    
        }
        else if (shroomie.type == MushroomType.NORMAL) {
            value += shroomie.getSize() * score;
        }
        else if (shroomie.type == MushroomType.GOOD) {
            value += shroomie.getSize() * 2 * score;
        }
        else if (shroomie.type == MushroomType.RARE) {
            value += shroomie.getSize() * 10 * score;
        }
        else if (shroomie.type == MushroomType.EGG)
        	value += shroomie.getSize() * score;
    }

    public void decrement(double amt) {
        value -= amt;
        clamp();
    }

    public void render(StateBasedGame game, Graphics g) {
        g.setColor(OFF);
        g.fillRect(x, y, WIDTH, HEIGHT);

        g.setColor(ON);
        float adjustment = HEIGHT * (value / max);
        g.fillRect(x, y + (HEIGHT - adjustment), WIDTH, adjustment);

        g.setColor(BORDER);
        g.drawRect(x, y, WIDTH, HEIGHT);

        g.setColor(Color.white);
    }

    public void update(StateBasedGame game, int delta) {
        clamp();
    }
    

    private void clamp() {
        if (value < 0) {
            value = 0;
        }
        if (value > max) {
            value = max;
        }
    }

    /**
     * It is no longer possible to reduce the amount to reward a player.
     * @return
     */
    public boolean tappedOut() {
        return score > MIN_SCORE;
    }

    /**
     * Reduce the amount that a mushroom refills the meter.
     */
    public void tap() {
        score -= SCORE_INCREMENT;
    }
}
