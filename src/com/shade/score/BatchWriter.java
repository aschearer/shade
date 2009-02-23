package com.shade.score;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.prefs.Preferences;

import com.shade.levels.LevelManager;

/**
 * Post all of the scores in a csv file to a server.
 *
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class BatchWriter {
    
    private static final String EMPTY_STRING = "";
    private static final String SCORE_KEY = "scores";
    private static final String SERVER = "http://anotherearlymorning.com/games/shade2/batch.php";
   
    
    public boolean write() {
        try {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < LevelManager.NUM_LEVELS; i++) {
                builder.append(collectScores(i));
            }
            String score = builder.toString();
            score = score.trim();
            String content = "scores=" + URLEncoder.encode(score, "US-ASCII");
            URL url = new URL(SERVER);
            URLConnection c = url.openConnection();
            c.setConnectTimeout(2000);
            c.setDoOutput(true);
            OutputStreamWriter o = new OutputStreamWriter(c.getOutputStream());
            // write the content
            o.write(content);
            o.flush();
            o.close();
            // read response and check for success
            BufferedReader i = new BufferedReader(new InputStreamReader(c
                    .getInputStream()));
            String response = i.readLine();
            return response.equals("success");
        } catch (Exception e) {
            return false;
        }
    }

    private String collectScores(int level) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        return prefs.get(SCORE_KEY + level, EMPTY_STRING);
    }
}
