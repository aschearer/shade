package com.shade.score;

import java.io.IOException;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import com.shade.util.CsvWriter;

public class LocalHighScoreWriter implements HighScoreWriter {

    private static final String EMPTY_STRING = "";
    private static final String SCORE_KEY = "scores";
    
    private static final int NAME = 0;
    private static final int SCORE = 1;
    private static final int LEVEL = 2;
    private static final int SPECIAL = 3;
    
    private static final char COMMA = ',';

    public boolean write(String name, int score, int level, boolean special) {
        String[] row = new String[4];
        row[NAME] = name;
        row[SCORE] = score + EMPTY_STRING;
        row[LEVEL] = level + EMPTY_STRING;
        row[SPECIAL] = (special) ? "1" : "0";
        return write(row[NAME], row[SCORE], row[LEVEL], row[SPECIAL]);
    }
    
    protected boolean write(String name, String score, String level, String special) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        StringWriter stream = new StringWriter();
        CsvWriter writer = new CsvWriter(stream, COMMA);
        String[] row = new String[] { name, score, level, special };
        try {
            writer.writeRecord(row);
//            writer.flush();
            
            stream.append(prefs.get(SCORE_KEY + level, EMPTY_STRING));
            prefs.put(SCORE_KEY + level, stream.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
