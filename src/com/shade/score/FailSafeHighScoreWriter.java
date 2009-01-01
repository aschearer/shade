package com.shade.score;

import java.util.prefs.Preferences;

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

    private static final String EMPTY_STRING = "";
    private static final String SCORE_KEY = "scores";
    private static final String SERVER = "http://anotherearlymorning.com/games/shade/post.php";

    private LocalHighScoreWriter localWriter;
    private RemoteHighScoreWriter remoteWriter;
    private BatchWriter batchWriter;

    public FailSafeHighScoreWriter() {
        localWriter = new LocalHighScoreWriter();
        remoteWriter = new RemoteHighScoreWriter(SERVER);
        batchWriter = new BatchWriter();
    }

    public boolean write(String name, int score, boolean clear) {
        // try to write remotely
        if (remoteWriter.write(name, score, clear)) {
            // try to write past local scores to server
            if (batchWriter.write()) {
                // clear the file
                Preferences.systemNodeForPackage(this.getClass()).put(SCORE_KEY, EMPTY_STRING);
            }
            // else do nothing, they will get written later
        } else {
            // can't connect to server, write locally
            return localWriter.write(name, score, clear);
        }
        // wrote current score successfully
        return true;
    }

}
