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
    private static final int CLEAR = 2;
    
    private static final char COMMA = ',';

    public boolean write(String name, int score, boolean clear) {
        String[] row = new String[3];
        row[NAME] = name;
        row[SCORE] = score + EMPTY_STRING;
        row[CLEAR] = (clear) ? "1" : "0";
        return write(row[NAME], row[SCORE], row[CLEAR]);
    }
    
    protected boolean write(String name, String score, String clear) {
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        StringWriter stream = new StringWriter();
        CsvWriter writer = new CsvWriter(stream, COMMA);
        String[] row = new String[] { name, score, clear };
        try {
            writer.writeRecord(row);
//            writer.flush();
            
            stream.append(prefs.get(SCORE_KEY, EMPTY_STRING));
            prefs.put(SCORE_KEY, stream.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
