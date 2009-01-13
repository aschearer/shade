package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;

public class CounterControl implements MushroomCounter, Animatable {

    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int MUSHROOM_SCORE = 20;
    
    //TODO: multiplier or lotsa points?
    public static final int TREASURE_SCORE = 1000;

    public int countDown, totalCount;

    private float x, y;
    private Image sprite;
    private TrueTypeFont font;
    private ScoreControl scorecard;

    public CounterControl(float x, float y, Image s, TrueTypeFont f) {
        this.x = x;
        this.y = y;
        sprite = s;
        font = f;
    }

    public void register(ScoreControl c) {
        scorecard = c;
    }

    public void onCollect(Mushroom shroomie) {
		if (shroomie.getRole() == Roles.TREASURE.ordinal()) {
			//WHEEE HACK! TODO: KILL HACK!
			scorecard.add(TREASURE_SCORE);
		} else {
	        scorecard.add(MUSHROOM_SCORE);
	        totalCount++;
	        if (countDown > 0) {
	            countDown--;
	        }
		}
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(x, y);
        float xBuffer = x + sprite.getWidth() + X_OFFSET;
        float yBuffer = y + Y_OFFSET;
        font.drawString(xBuffer, yBuffer, "" + countDown);
    }

    public void update(StateBasedGame game, int delta) {
        // doesn't need to be updated
    }

    public void reset(int par) {
        countDown = par;
        totalCount = 0;
    }
    
    public int totalCount() {
        return totalCount;
    }
    
    public boolean parWasMet() {
        return countDown == 0;
    }

}
