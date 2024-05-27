import javax.swing.*;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Controller implements KeyListener, ActionListener {

    private Model model;
    private Viewer viewer;
    private Connection connection;
    private Map<String, Runnable> commandsMap;
    private Map<Integer, Integer> playerOneKeyMap;
    private Map<Integer, Integer> playerTwoKeyMap;
    private AudioControlApp sound = new AudioControlApp();

    public Controller(Viewer viewer) {
        model = new Model(viewer);
        this.viewer = viewer;

        commandsMap = initCommands();

        playerOneKeyMap = new HashMap<>();
        playerOneKeyMap.put(KeyEvent.VK_UP, Model.Direction.UP);
        playerOneKeyMap.put(KeyEvent.VK_DOWN, Model.Direction.DOWN);
        playerOneKeyMap.put(KeyEvent.VK_LEFT, Model.Direction.LEFT);
        playerOneKeyMap.put(KeyEvent.VK_RIGHT, Model.Direction.RIGHT);

        playerTwoKeyMap = new HashMap<>();
        playerTwoKeyMap.put(KeyEvent.VK_W, Model.Direction.UP);
        playerTwoKeyMap.put(KeyEvent.VK_S, Model.Direction.DOWN);
        playerTwoKeyMap.put(KeyEvent.VK_A, Model.Direction.LEFT);
        playerTwoKeyMap.put(KeyEvent.VK_D, Model.Direction.RIGHT);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        Runnable runnable = commandsMap.get(command);

        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public synchronized void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        Integer direction = playerOneKeyMap.get(keyCode);
        if (direction != null) {
            model.movePlayerOne(direction);

        }

        if (connection != null && (!connection.isExit())) {
            connection.sendMessage(Connection.START_RECEIVING);
            connection.sendMessage(Connection.UPDATE_GAME);
            sendToConnection();
            return;
        }

        direction = playerTwoKeyMap.get(keyCode);
        if (direction != null && model.isTwoPlayerGame()) {
            model.movePlayerTwo(direction);
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    public void addCommand(String command, Runnable runnable) {
        commandsMap.put(command, runnable);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    private Map<String, Runnable> initCommands() {
        Map<String, Runnable> commandsMap = new HashMap<>();

        commandsMap.put("MenuSingleplayerCommand", this::showMenuSinglePlayer);
        commandsMap.put("GameStartOnePlayerCommand", this::startOnePlayerGame);
        commandsMap.put("GameStartTwoPlayerCommand", this::startTwoPlayerGame);
        commandsMap.put("MenuMultiplayerCommand", this::showMenuMultiplayer);
        commandsMap.put("GameStartAsServerCommand", this::startAsServer);
        commandsMap.put("GameStartAsClientCommand", this::startAsClient);
        commandsMap.put("NewGameCommand", this::newGame);
        commandsMap.put("NextCommand", this::nextLevel);
        commandsMap.put("RestartCommand", this::restartLevel);
        commandsMap.put("ToStartMenuCommand", this::toStartMenu);
        commandsMap.put("CloseCommand", this::close);
        commandsMap.put("UpdateModelFromServerCommand", this::receiveFromConnection);
        commandsMap.put("ConnectionClosedCommand", this::connectionClosed);
        commandsMap.put("InitGameCommand", this::initFromConnection);
        commandsMap.put("SettingCommand", () -> {
            sound.showWindow();
        });

        // commands for levels menu
        final int LEVELS = model.getLevelsRepository().getLevelsCounter();
        for (int i = 1; i <= LEVELS; i++) {
            String commandName = "Level" + i + "Command";
            final int k = i;
            commandsMap.put(commandName, () -> {
                if (connection != null && connection.isServer()) {
                    model.startLevel("CLIENT", k);
                } else if (connection != null && (!connection.isServer())) {
                    model.startLevel("SERVER", k);
                } else {
                    model.startLevel(k);
                }
            });
        }

        return commandsMap;
    }

    private void showMenuSinglePlayer() {
        viewer.showCard("SINGLEPLAYER");
    }

    private void showMenuMultiplayer() {
        viewer.showCard("MULTIPLAYER");
    }

    private void startOnePlayerGame() {
        model.setTwoPlayersGame(false);
        model.setClientGame(false);
        model.startGame();
        viewer.showCard("GAME");
    }

    private void startTwoPlayerGame() {
        model.setTwoPlayersGame(true);
        model.setClientGame(false);
        model.startGame();
        viewer.showCard("GAME");
    }

    private void startAsServer() {
        try {
            model.setTwoPlayersGame(true);
            model.setClientGame(false);
            model.startGame("SERVER");
            connection = Connection.getServerConnection(this, 6000);

            sendToConnection();

            connection.start();

            viewer.showCard("GAME");
        } catch (IOException ioe) {
            viewer.showErrorDialog(Viewer.MessageType.ERROR_CONNECTION);
            viewer.showCard("MULTIPLAYER");
            System.out.println(ioe);
        }
    }

    private void startAsClient() {
        try {
            model.setTwoPlayersGame(true);
            model.setClientGame(true);
            connection = Connection.getClientConnection(this, "localhost", 6000);

            initFromConnection();

            connection.start();
        } catch (IOException ioe) {
            viewer.showErrorDialog(Viewer.MessageType.ERROR_CONNECTION);
            viewer.showCard("MULTIPLAYER");
            System.out.println(ioe);
        }
    }

    private void newGame() {
        model.startGame();

        if (connection != null && connection.isServer()) {
            connection.sendMessage(Connection.START_RECEIVING);
            connection.sendMessage(Connection.INIT_GAME);
            sendToConnection();
        }
        viewer.showCard("GAME");
    }

    private void nextLevel() {
        int level = model.getSelectedLevel();
        level = level + 1;
        model.startLevel(level);

        if (connection != null && connection.isServer()) {
            connection.sendMessage(Connection.START_RECEIVING);
            connection.sendMessage(Connection.INIT_GAME);
            sendToConnection();
        }
        viewer.showCard("GAME");
    }

    private void restartLevel() {
        int level = model.getSelectedLevel();
        model.startLevel(level);
        
        if (connection != null && connection.isServer()) {
            connection.sendMessage(Connection.START_RECEIVING);
            connection.sendMessage(Connection.INIT_GAME);
            sendToConnection();
        }
        viewer.showCard("GAME");
    }

    private void toStartMenu() {
        closeConnectionIfOpen();
        viewer.showCard("START");
    }

    private void close() {
        closeConnectionIfOpen();
        viewer.dispose();
    }

    private void connectionClosed() {
        closeConnectionIfOpen();
        viewer.showErrorDialog(Viewer.MessageType.ERROR_CONNECTION);
        viewer.showCard("MULTIPLAYER");
    }

    private void closeConnectionIfOpen() {
        if (connection != null) {
            connection.closeConnection();
        }
    }

    private void receiveFromConnection() {
        DataWrapper resivedData = connection.getReceivedData();

        int[][] playZone = resivedData.getPlayZone();
        int direction = resivedData.getPlayerDirection();
        int x = resivedData.getPlayerX();
        int y = resivedData.getPlayerY();
        int matchedGoalsCounter = resivedData.getMatchedGoalsCounter();
        int winsCounter = resivedData.getPlayerWinsCounter();

        Movable player = model.getPlayerTwo();
        player.setDirection(direction);
        player.setX(x);
        player.setY(y);

        model.setPlayerTwoMathcedGoalsCounter(matchedGoalsCounter);
        model.setPlayerTwoWinsCounter(winsCounter);

        model.updatePlayerTwoPlayZone(playZone);
    }

    private void sendToConnection() {
        try {
            int[][] playZone = model.getPlayerOnePlayZone();
            Movable player = model.getPlayerOne();
            int direction = player.getDirection();
            int x = player.getX();
            int y = player.getY();
            int matchedGoalsCounter = model.getPlayerOneMathcedGoalsCounter();
            int playerWins = model.getPlayerOneWinsCounter();

            DataWrapper sendingData = new DataWrapper(playZone,
                                                      direction,
                                                      x,
                                                      y,
                                                      matchedGoalsCounter,
                                                      playerWins);

            connection.sendChanges(sendingData);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private void initFromConnection() {
        try {
            DataWrapper resivedData = connection.receiveChanges();
            int[][] playZone = resivedData.getPlayZone();

            model.intiPlayZone("CLIENT", playZone);

            viewer.showCard("GAME");
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
