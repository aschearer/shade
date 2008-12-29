package com.shade.score;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.newdawn.slick.util.ResourceLoader;

import com.shade.util.CsvWriter;

public class LocalHighScoreWriter implements HighScoreWriter {

    private static final int NAME = 0;
    private static final int SCORE = 1;
    private static final int CLEAR = 2;
    
    private static final char COMMA = ',';
    private CsvWriter writer;

    public LocalHighScoreWriter(String path) {
        try {
            URL url = ResourceLoader.getResource(path);
            FileWriter stream = new FileWriter(url.getPath(), true);
            writer = new CsvWriter(stream, COMMA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean write(String name, int score, boolean clear) {
        String[] row = new String[3];
        row[NAME] = name;
        row[SCORE] = score + "";
        row[CLEAR] = (clear) ? "1" : "0";
        return write(row[NAME], row[SCORE], row[CLEAR]);
    }
    
    protected boolean write(String name, String score, String clear) {
        String[] row = new String[] { name, score, clear };
        try {
            writer.writeRecord(row);
            writer.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
