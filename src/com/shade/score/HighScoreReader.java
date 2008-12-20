package com.shade.score;

import org.newdawn.slick.SlickException;

public interface HighScoreReader {

    /**
     * Return an ordered list of scores.
     * 
     * @param limit Return this many, set to zero to return all.
     * @return
     */
    public String[] getScores(int limit) throws SlickException;
}
