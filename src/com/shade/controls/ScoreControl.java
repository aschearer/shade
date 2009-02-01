package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class ScoreControl implements Animatable {

    private float x, y;
    private TrueTypeFont font;
    private float totalScore, levelScore, currentScore;
    private boolean gameCleared;
    //score spiker
    private int base;

    public ScoreControl(float x, float y, TrueTypeFont f) {
        this.x = x;
        this.y = y;
        font = f;
        base = 1;
    }

    public void render(StateBasedGame game, Graphics g) {
        font.drawString(x, y, "" + (int) currentScore);
    }

    public void update(StateBasedGame game, int delta) {
        if (currentScore + base < totalScore) {
            currentScore+= base;
//            base++;
        }
        else if(currentScore<totalScore){
        	currentScore = totalScore;
        }
    }

    public void add(float points) {
        levelScore += points;
        totalScore += points;
    }

    public void setBeaten() {
        gameCleared = true;
    }

    public void startLevel() {
        levelScore = 0;
    }

    public void rollbackLevel() {
        totalScore -= levelScore;
        currentScore = totalScore;
        levelScore = 0;
    }

    public void reset() {
        totalScore = 0;
        levelScore = 0;
        currentScore = 0;
        gameCleared = false;
    }

    public int getScore() {
        return (int) Math.floor(totalScore);
    }

    public int getLevelScore() {
        return (int) levelScore;
    }
    
    public boolean isGameBeaten() {
        return gameCleared;
    }

}
