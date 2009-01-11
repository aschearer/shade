package com.shade.score;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;

import com.shade.util.CsvReader;

/**
 * Read high scores from a remote server.
 * 
 * This performs a get request to retrieve a list of high scores from the
 * server. It expects the high scores to be in CSV format.
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class RemoteHighScoreReader implements HighScoreReader {

    private String base;

    public RemoteHighScoreReader(String path) {
        base = path;
    }

    public String[][] getScores(int level, int limit) {
        try {
            String target = base + "?num_scores="
                    + URLEncoder.encode("" + limit, "US-ASCII");
            target += "&level=" + URLEncoder.encode("" + level, "US-ASCII");
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
