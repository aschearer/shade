package com.shade.score;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RemoteHighScoreWriter implements HighScoreWriter {

    private String base;

    public RemoteHighScoreWriter(String path) {
        base = path;
    }

    public boolean write(String name, int score, int level, boolean special) {
        String s = (special) ? "1" : "0";
        return write(name, score + "", level + "", s);
    }

    protected boolean write(String name, String score, String level, String special) {
        try {
            String content = "name=" + URLEncoder.encode(name, "US-ASCII");
            content += "&score=" + score;
            content += "&level=" + level;
            content += "&special=" + special;
            URL url = new URL(base);
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

}
