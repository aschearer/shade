package com.shade.score;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.newdawn.slick.SlickException;

import com.shade.util.CsvReader;

public class RemoteHighScoreReader implements HighScoreReader {

    private String base;

    public RemoteHighScoreReader(String path) {
        base = path;
    }

    public String[][] getScores(int limit) throws SlickException {
        try {
            String target = base + "?num_scores="
                    + URLEncoder.encode("" + limit, "UTF-8");
            // open connection to read

            URL url = new URL(target);
            URLConnection c = url.openConnection();
            c.setConnectTimeout(1000);
            InputStreamReader i = new InputStreamReader(c.getInputStream());

            CsvReader reader = new CsvReader(i);

            // read lines into an array and return
            LinkedList<String[]> rows = new LinkedList<String[]>();
            while (reader.readRecord()) {
                String[] row = new String[3];
                row[0] = reader.get(0);
                row[1] = reader.get(1);
                row[2] = reader.get(2);
                rows.add(row);
            }
            return rows.toArray(new String[0][0]);
        } catch (Exception e) {
            return null;
        }

    }
}
