package com.shade.score;

/**
 * Reader which will fall back to the local high score list if it cannot connect
 * to the server.
 *  
 * @author Alexander Schearer <aschearer@gmail.com>
 */
public class FailSafeHighScoreReader implements HighScoreReader {

    private static final String SERVER = "http://anotherearlymorning.com/games/shade2/board.php";
    
    private LocalHighScoreReader localReader;
    private RemoteHighScoreReader remoteReader;
    private boolean local;
    
    public boolean isLocal() {
        return local;
    }

    public FailSafeHighScoreReader() {
        localReader = new LocalHighScoreReader();
        remoteReader = new RemoteHighScoreReader(SERVER);
    }

    public String[][] getScores(int level, int limit) {
        local = false;
        String[][] scores = remoteReader.getScores(level, limit);
        if (scores == null) {
            local = true;
            scores = localReader.getScores(level, limit);
        }
        return scores;
    }

}
