package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.entities.mushroom.Mushroom;

public class CounterControl implements MushroomCounter, Animatable {

    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int MUSHROOM_SCORE = 20;

    public int value;

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
        scorecard.add(MUSHROOM_SCORE);
        value++;
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(x, y);
        float xBuffer = x + sprite.getWidth() + X_OFFSET;
        float yBuffer = y + Y_OFFSET;
        font.drawString(xBuffer, yBuffer, "" + value);
    }

    public void update(StateBasedGame game, int delta) {
        // doesn't need to be updated
    }

    public void reset() {
        value = 0;
    }

}
