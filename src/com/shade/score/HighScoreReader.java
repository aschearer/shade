package com.shade.score;

public interface HighScoreReader {

    /**
     * Return an ordered list of scores.
     * 
     * @param level The level to fetch the scores for.
     * @param limit Return this many, set to zero to return all.
     * @return
     */
    public String[][] getScores(int level, int limit);
}
