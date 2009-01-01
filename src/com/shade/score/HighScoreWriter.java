package com.shade.score;

public interface HighScoreWriter {

    public boolean write(String name, int score, boolean clear);
}
