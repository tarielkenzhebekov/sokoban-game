public class Model {

    private Viewer viewer;

    private LevelsRepository levelsRepository;
    private int[][] playerOnePlayZone;
    private int[][] playerTwoPlayZone;
    private int[][] playZoneRestore;
    private int selectedLevel;

    private SoundsRepository soundsRepository;

    private Movable playerOne;
    private Movable playerTwo;
    private Movable movingBox;

    private int[] countOfElements;
    private int[] playerOneMatchedGoalsCounter;
    private int[] playerTwoMatchedGoalsCounter;
    private int playerOneWinsCounter;
    private int playerTwoWinsCounter;

    private String southPanelText;
    private boolean twoPlayersGame;
    private boolean clientGame;

    public Model(Viewer viewer) {
        this.viewer = viewer;

        levelsRepository = new LevelsRepository();
        selectedLevel = 1;

        this.soundsRepository = new SoundsRepository();

        Sound playerSound = soundsRepository.getSound("PlayerSound");
        playerOne = new Movable(GameElement.PLAYER, -1, -1, playerSound);
        playerTwo = new Movable(GameElement.PLAYER, -1, -1, playerSound);

        Sound boxSound = soundsRepository.getSound("BoxSound");
        movingBox = new Movable(GameElement.BOX, -1, -1, boxSound);

        playerOneMatchedGoalsCounter = new int[1];
        playerTwoMatchedGoalsCounter = new int[1];

        southPanelText = "";
        twoPlayersGame = false;
        clientGame = false;
    }

    public void startGame() {
        southPanelText = "ONE PLAYER";
        if (isTwoPlayerGame()) {
            southPanelText = "TWO PLAYER";
        }
        startGame(southPanelText);
    }

    public void startGame(String southPanelText) {
        selectedLevel = 1;

        startLevel(southPanelText, selectedLevel);
        playerOneWinsCounter = 0;
        playerTwoWinsCounter = 0;
    }

    public void startLevel(int levelNumber) {
        startLevel(southPanelText, levelNumber);
    }

    public void startLevel(String southPanelText, int levelNumber) {
        selectedLevel = levelNumber;

        Level level = levelsRepository.getLevel(selectedLevel);
        int[][] playZone = level.getPlayZone();
        intiPlayZone(southPanelText, playZone);
    }



    public void movePlayerOne(int direction) {
        movePlayer(playerOne, playerOnePlayZone, playerOneMatchedGoalsCounter, direction);
    }

    public void movePlayerTwo(int direction) {
        movePlayer(playerTwo, playerTwoPlayZone, playerTwoMatchedGoalsCounter, direction);
    }

    public void updatePlayerOnePlayZone(int[][] playZone) {
        playerOnePlayZone = playZone;
        viewer.update();
        won();
    }

    public void updatePlayerTwoPlayZone(int[][] playZone) {
        playerTwoPlayZone = playZone;
        viewer.update();
        won();
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public int[][] getPlayerOnePlayZone() {
        return playerOnePlayZone;
    }

    public int[][] getPlayerTwoPlayZone() {
        return playerTwoPlayZone;
    }

    public Movable getPlayerOne() {
        return playerOne;
    }

    public Movable getPlayerTwo() {
        return playerTwo;
    }

    public int getPlayerOneMathcedGoalsCounter() {
        return playerOneMatchedGoalsCounter[0];
    }

    public int getPlayerTwoMatchedGoalsCounter() {
        return playerTwoMatchedGoalsCounter[0];
    }

    public int getPlayerOneWinsCounter() {
        return playerOneWinsCounter;
    }

    public int getPlayerTwoWinsCounter() {
        return playerTwoWinsCounter;
    }

    public void setPlayerOneMathcedGoalsCounter(int playerOneMatchedGoalsCounter) {
        this.playerOneMatchedGoalsCounter[0] = playerOneMatchedGoalsCounter;
    } 

    public void setPlayerTwoMathcedGoalsCounter(int playerTwoMathcedGoalsCounter) {
        this.playerTwoMatchedGoalsCounter[0] = playerTwoMathcedGoalsCounter;
    }

    public void setPlayerOneWinsCounter(int playerOneWinsCounter) {
        this.playerOneWinsCounter = playerOneWinsCounter;
    }

    public void setPlayerTwoWinsCounter(int playerTwoWinsCounter) {
        this.playerTwoWinsCounter = playerTwoWinsCounter;
    }
    
    public void setTwoPlayersGame(boolean twoPlayersGame) {
        this.twoPlayersGame = twoPlayersGame;
    }

    public void setClientGame(boolean clientGame) {
        this.clientGame = clientGame;
    }

    public boolean isTwoPlayerGame() {
        return twoPlayersGame;
    }

    public boolean isClientGame() {
        return clientGame;
    }

    public LevelsRepository getLevelsRepository() {
        return levelsRepository;
    }

    public void intiPlayZone(String southPanelText, int[][] playZone) {
        int result = checkPlayZone(playZone);
        if (result != -1) {
            viewer.showErrorDialog(result);
            viewer.showCard("START");
            return;
        }

        playerOnePlayZone = playZone;
        playerTwoPlayZone = copyArray(playZone);

        playZoneRestore = copyArray(playZone);
        for (int i = 0; i < playZoneRestore.length; i++) {
            for (int j = 0; j < playZoneRestore[i].length; j++) {
                if (playZoneRestore[i][j] != GameElement.GOAL) {
                    if (playZoneRestore[i][j] == GameElement.PLAYER) {
                        final int x = j;
                        final int y = i;

                        playerOne.setX(x);
                        playerOne.setY(y);

                        playerTwo.setX(x);
                        playerTwo.setY(y);
                    }
                    playZoneRestore[i][j] = GameElement.BLANK;
                }
            }
        }

        playerOne.setDirection(Direction.DOWN);
        playerTwo.setDirection(Direction.DOWN);
        playerOneMatchedGoalsCounter[0] = 0;
        playerTwoMatchedGoalsCounter[0] = 0;

        viewer.update();
        viewer.setTextToStatusPanel(southPanelText, selectedLevel);
    }

    private int checkPlayZone(int[][] playZone) {
        if (playZone == null) {
            return Viewer.MessageType.ERROR_PLAY_ZONE_IS_NULL;
        }

        countOfElements = countElements(playZone);
        if (countOfElements[GameElement.PLAYER] != 1) {
            return Viewer.MessageType.ERROR_TOO_MANY_OR_NO_PLAYER;
        }

        int boxes = countOfElements[GameElement.BOX];
        int goals = countOfElements[GameElement.GOAL];
        if ((boxes == 0)
                || (goals == 0)) {
            return Viewer.MessageType.ERROR_NO_BOXES_OR_GOALS;
        }

        if (boxes != goals) {
            return Viewer.MessageType.ERROR_UNMATCH_BOXES_AND_GOALS;
        }

        return -1;
    }

    private int[] countElements(int[][] playZone) {
        countOfElements = new int[GameElement.GOAL + 1];

        for (int i = 0; i < playZone.length; i++) {
            for (int j = 0; j < playZone[i].length; j++) {
                if (playZone[i][j] == GameElement.PLAYER) {
                    countOfElements[GameElement.PLAYER] = countOfElements[GameElement.PLAYER] + 1;
                } else if (playZone[i][j] == GameElement.BOX) {
                    countOfElements[GameElement.BOX] = countOfElements[GameElement.BOX] + 1;
                } else if (playZone[i][j] == GameElement.GOAL) {
                    countOfElements[GameElement.GOAL] = countOfElements[GameElement.GOAL] + 1;
                }
            }
        }

        return countOfElements;
    }

    private int[][] copyArray(int[][] array) {
        int[][] copy = new int[array.length][];

        for (int i = 0; i < array.length; i++) {
            copy[i] = array[i].clone();
        }

        return copy;
    }

    private void movePlayer(Movable player, int[][] playZone, int[] matchedGoalCounter, int direction) {
        int[] offsets = getOffsets(direction);

        final int offsetX = offsets[0];
        final int offsetY = offsets[1];
        player.setDirection(direction);
        move(player, playZone, matchedGoalCounter, offsetX, offsetY);
        viewer.update();

        won();
    }

    private int[] getOffsets(int direction) {
        int offsetX = 0;
        int offsetY = 0;

        if (direction == Direction.DOWN) {
            offsetY = 1;
        } else if (direction == Direction.LEFT) {
            offsetX = -1;
        } else if (direction == Direction.RIGHT) {
            offsetX = 1;
        } else if (direction == Direction.UP) {
            offsetY = -1;
        } else {
            return null;
        }

        int[] offsets = new int[2];
        offsets[0] = offsetX;
        offsets[1] = offsetY;

        return offsets;
    }

    private boolean move(Movable movable, int[][] playZone, int[] matchedGoalCounter, int offsetX, int offsetY) {
        int x = movable.getX();
        int y = movable.getY();
        int newX = x + offsetX;
        int newY = y + offsetY;

        boolean canMove = isCanMove(playZone, newX, newY);
        if (!canMove) {
            soundsRepository.playSound("WallSound");
            return canMove;
        }

        boolean moved = true;
        if ((movable.getGameElement() != GameElement.BOX)
                && (playZone[newY][newX] == GameElement.BOX)) {
            movingBox.setX(newX);
            movingBox.setY(newY);

            moved = move(movingBox, playZone, matchedGoalCounter, offsetX, offsetY);
        } else if (movable.getGameElement() == GameElement.BOX
                && playZone[newY][newX] == GameElement.BOX) {
            soundsRepository.playSound("WallSound");
            moved = false;
        }

        if (moved) {
            if ((movable.getGameElement() == GameElement.BOX)
                    && (playZone[newY][newX] == GameElement.GOAL)) {
                matchedGoalCounter[0] = matchedGoalCounter[0] + 1;
                soundsRepository.playSound("GoalSound");
            }
            if (movable.getGameElement() == GameElement.BOX
                    && playZoneRestore[y][x] == GameElement.GOAL) {
                matchedGoalCounter[0] = matchedGoalCounter[0] - 1;
            }

            playZone[y][x] = playZoneRestore[y][x];
            playZone[newY][newX] = movable.getGameElement();

            movable.setX(newX);
            movable.setY(newY);
            movable.playSound();
        }

        return moved;
    }

    private boolean isCanMove(int[][] playZone, int newX, int newY) {
        if (newY >= playZone.length || newY < 0) {
            return false;
        } else if (newX >= playZone[newY].length || newX < 0) {
            return false;
        }

        if (playZone[newY][newX] == GameElement.WALL) {
            return false;
        }

        return true;
    }

    private void won() {
        String winCard = null;

        if (isClientGame()) {
            winCard = getWinCardForClientGame();
        } else if (isTwoPlayerGame()) {
            winCard = getWinCardForTwoPlayerGame();
        } else {
            winCard = getWinCardForOnePlayerGame();
        }

        if (winCard != null) {
            soundsRepository.playSound("WinSound");
            viewer.showCard(winCard);
        }
    }

    private String getWinCardForClientGame() {
        String winCard = null;

        if (playerOneMatchedGoalsCounter[0] == countOfElements[GameElement.GOAL]) {
            playerOneWinsCounter = playerOneWinsCounter + 1;
            winCard = "CLIENT_PLAYER_ONE_WIN_LEVEL";
            winCard = getWinCardForWinningClientPlayer(winCard);
        } else if (playerTwoMatchedGoalsCounter[0] == countOfElements[GameElement.GOAL]) {
            playerTwoWinsCounter = playerTwoWinsCounter + 1;
            winCard = "CLIENT_PLAYER_TWO_WIN_LEVEL";
            winCard = getWinCardForWinningClientPlayer(winCard);
        }

        return winCard;
    }

    private String getWinCardForWinningClientPlayer(String previosWinCard) {
        if ((selectedLevel + 1) > levelsRepository.getLevelsCounter()) {
            if (playerOneWinsCounter > playerTwoWinsCounter) {
                previosWinCard = "CLIENT_PLAYER_ONE_WIN_GAME";
            } else if (playerTwoWinsCounter > playerOneWinsCounter) {
                previosWinCard = "CLIENT_PLAYER_TWO_WIN_GAME";
            } else {
                previosWinCard = "CLIENT_DRAW";
            }
        }

        return previosWinCard;
    }

    private String getWinCardForTwoPlayerGame() {
        String winCard = null;

        if (playerOneMatchedGoalsCounter[0] == countOfElements[GameElement.GOAL]) {
            playerOneWinsCounter = playerOneWinsCounter + 1;
            winCard = "PLAYER_ONE_WIN_LEVEL";
            winCard = getWinCardForWinningPlayer(winCard);
        } else if (playerTwoMatchedGoalsCounter[0] == countOfElements[GameElement.GOAL]) {
            playerTwoWinsCounter = playerTwoWinsCounter + 1;
            winCard = "PLAYER_TWO_WIN_LEVEL";
            winCard = getWinCardForWinningPlayer(winCard);
        }

        return winCard;
    }

    private String getWinCardForWinningPlayer(String previosWinCard) {
        if ((selectedLevel + 1) > levelsRepository.getLevelsCounter()) {
            if (playerOneWinsCounter > playerTwoWinsCounter) {
                previosWinCard = "PLAYER_ONE_WIN_GAME";
            } else if (playerTwoWinsCounter > playerOneWinsCounter) {
                previosWinCard = "PLAYER_TWO_WIN_GAME";
            } else {
                previosWinCard = "DRAW";
            }
        }

        return previosWinCard;
    }

    private String getWinCardForOnePlayerGame() {
        String winCard = null;

        if (playerOneMatchedGoalsCounter[0] == countOfElements[GameElement.GOAL]) {
            winCard = "WIN_LEVEL";
            if ((selectedLevel + 1) > levelsRepository.getLevelsCounter()) {
                winCard = "WIN_GAME";
            }
        }

        return winCard;
    }

    public static final class Direction {

        public static final int NON = 0;
        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        public static final int UP = 3;
        public static final int DOWN = 4;

        private Direction() {
        }
    }

    public static final class GameElement {

        public static final int BLANK = 0;
        public static final int PLAYER = 1;
        public static final int WALL = 2;
        public static final int BOX = 3;
        public static final int GOAL = 4;

        private GameElement() {
        }
    }
}
