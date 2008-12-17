package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class ScoreControl implements Animatable {

    private float currentScore, finalScore;
    private float x, y;
    private TrueTypeFont font;
    
    public ScoreControl(float x, float y, TrueTypeFont f) {
        this.x = x;
        this.y = y;
        font = f;
    }

    public void reset() {
        currentScore = 0;
        finalScore = 0;
    }

    public void add(float points) {
        finalScore += points;
    }

    public int read() {
        return (int) Math.floor(finalScore);
    }

    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, "" + readCurrentScore());
    }

    public void update(StateBasedGame game, int delta) {
        if (readCurrentScore() < read()) {
            currentScore++;
        }
    }
    
    private int readCurrentScore() {
        return (int) Math.floor(finalScore);
    }
}
