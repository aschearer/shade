package com.shade.score;

import java.util.prefs.Preferences;

import com.shade.levels.LevelManager;

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
    private static final String SERVER = "http://anotherearlymorning.com/games/shade2/post.php";

    private LocalHighScoreWriter localWriter;
    private RemoteHighScoreWriter remoteWriter;
    private BatchWriter batchWriter;

    public FailSafeHighScoreWriter() {
        localWriter = new LocalHighScoreWriter();
        remoteWriter = new RemoteHighScoreWriter(SERVER);
        batchWriter = new BatchWriter();
    }

    public boolean write(String name, int score, int level, boolean special) {
        // try to write remotely
        if (remoteWriter.write(name, score, level, special)) {
            // try to write past local scores to server
            if (batchWriter.write()) {
                // clear the files
                for (int i = 0; i < LevelManager.NUM_LEVELS; i++) {
                    Preferences.userNodeForPackage(this.getClass()).put(SCORE_KEY + i, EMPTY_STRING);
                }
            }
            // else do nothing, they will get written later
        } else {
            // can't connect to server, write locally
            return localWriter.write(name, score, level, special);
        }
        // wrote current score successfully
        return true;
    }

}
