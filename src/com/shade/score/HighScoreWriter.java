package com.shade.score;

import org.newdawn.slick.SlickException;

public interface HighScoreWriter {

    public boolean write(String name, int score, boolean clear) throws SlickException;
}
