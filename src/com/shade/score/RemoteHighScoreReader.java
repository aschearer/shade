package com.shade.score;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.newdawn.slick.SlickException;

public class RemoteHighScoreReader implements HighScoreReader {

    private String base;

    public RemoteHighScoreReader(String path) {
        base = path;
    }

    public String[] getScores(int limit) throws SlickException {
        try {
            String target = base + "?num_scores="
                    + URLEncoder.encode("" + limit, "UTF-8");
            // open connection to read

            URL url = new URL(target);
            URLConnection c = url.openConnection();
            BufferedReader i = new BufferedReader(new InputStreamReader(c
                    .getInputStream()));
            // read lines into an array and return
            LinkedList<String> lines = new LinkedList<String>();
            String line;
            while ((line = i.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[0]);
        } catch (Exception e) {
            throw new SlickException("Failed to read high scores.");
        }

    }
}
