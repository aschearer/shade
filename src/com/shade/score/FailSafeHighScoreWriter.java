package com.shade.score;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.newdawn.slick.util.ResourceLoader;

/**
 * Write high scores to a remote server or locally if you cannot connect to the
 * server.
 * 
 * Try to write current score to server
 *   If failed then write locally, exit
 *   If successful try to write each local score to server, continue
 *     If failed then quit, exit
 *     If successful then remove from local file, continue
 * 
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class FailSafeHighScoreWriter implements HighScoreWriter {

    private static final String FILE = "states/highscore/scores.csv";
    private static final String SERVER = "http://anotherearlymorning.com/games/shade/post.php";

    private LocalHighScoreWriter localWriter;
    private RemoteHighScoreWriter remoteWriter;
    private BatchWriter batchWriter;

    public FailSafeHighScoreWriter() {
        localWriter = new LocalHighScoreWriter(FILE);
        remoteWriter = new RemoteHighScoreWriter(SERVER);
        batchWriter = new BatchWriter(FILE);
    }

    public boolean write(String name, int score, boolean clear) {
        // try to write remotely
        if (remoteWriter.write(name, score, clear)) {
            // try to write past local scores to server
            if (batchWriter.write()) {
                // clear the file
                clearFile(FILE);
            }
            // else do nothing, they will get written later
        } else {
            // can't connect to server, write locally
            return localWriter.write(name, score, clear);
        }
        // wrote current score successfully
        return true;
    }

    private void clearFile(String f) {
        try {
            URL u = ResourceLoader.getResource(f);
            FileWriter w = new FileWriter(u.getPath());
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
