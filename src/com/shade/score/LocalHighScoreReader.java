package com.shade.score;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import com.shade.util.CsvReader;

/**
 * Read high scores from a csv file located in the jar.
 * 
 * This is useful if the remote server cannot be reached.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class LocalHighScoreReader implements HighScoreReader {

    private static final String EMPTY_STRING = "";
    private static final String SCORE_KEY = "scores";
    
    private static final int NAME = 0;
    private static final int SCORE = 1;
    private static final int CLEAR = 2;

    /**
     * Returns all the scores if zero is passed.
     */
    public String[][] getScores(int level, int limit) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        StringReader s = new StringReader(prefs.get(SCORE_KEY + level, EMPTY_STRING));
        CsvReader reader = new CsvReader(s);
        LinkedList<String[]> rows = new LinkedList<String[]>();
        try {
            while (reader.readRecord()) {
                String[] row = new String[3];
                row[NAME] = reader.get(NAME);
                row[SCORE] = reader.get(SCORE);
                row[CLEAR] = reader.get(CLEAR);
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Collections.sort(rows, new Comparator<String[]>() {

            public int compare(String[] s1, String[] s2) {
                return s1[SCORE].compareTo(s2[SCORE]);
            }
            
        });
        
        return firstN(rows, limit);
    }

    private String[][] firstN(LinkedList<String[]> rows, int limit) {
        limit = (limit == 0) ? rows.size() : limit;
        int size = (rows.size() > limit) ? limit : rows.size();
        String[][] n = new String[size][3];
        
        for (int i = 0; i < size; i++) {
            n[i][NAME] = rows.get(i)[NAME];
            n[i][SCORE] = rows.get(i)[SCORE];
            n[i][CLEAR] = rows.get(i)[CLEAR];
        }
        return n;
    }

}
