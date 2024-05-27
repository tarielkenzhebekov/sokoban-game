public class DataWrapper {
    
    private int[][] playZone;
    private int playerDirection;
    private int playerX;
    private int playerY;
    private int matchedGoalsCounter;
    private int playerWinsCounter;

    public DataWrapper(int[][] playZone, 
                       int playerDirection, 
                       int playerX, 
                       int playerY, 
                       int matchedGoalsCounter, 
                       int playerWinsCounter) {

        this.playZone = playZone;
        this.playerDirection = playerDirection;
        this.playerX = playerX;
        this.playerY = playerY;
        this.matchedGoalsCounter = matchedGoalsCounter;
        this.playerWinsCounter = playerWinsCounter;
    }

    public int[][] getPlayZone() {
        return playZone;
    }

    public int getPlayerDirection() {
        return playerDirection;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getMatchedGoalsCounter() {
        return matchedGoalsCounter;
    }

    public int getPlayerWinsCounter() {
        return playerWinsCounter;
    }

}
