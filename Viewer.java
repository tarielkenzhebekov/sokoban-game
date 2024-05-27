import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.JButton;

import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;

public class Viewer {

    private JFrame frame;
    private JPanel cardPane;
    private Canvas canvas;
    private Controller controller;
    private ArrayList<JMenuItem> disablableItems;
    private JLabel currentLevelNumber;
    private JPanel statusPanel;

    public Viewer(String style, Font font) {
        controller = new Controller(this);
        Model model = controller.getModel();
        canvas = new Canvas(model, style);

        disablableItems = new ArrayList<>();
        JMenuBar menuBar = createJMenuBar(controller, font);
        disablableItems.forEach((item -> {
            item.setEnabled(false);
        }));

        statusPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 10, 10);
        statusPanel.setLayout(flowLayout);

        currentLevelNumber = new JLabel("LEVEL: 1");
        currentLevelNumber.setFont(font);

        statusPanel.add(currentLevelNumber);
        statusPanel.add(Box.createHorizontalGlue());

        cardPane = createCardJPanel(controller, font);

        frame = new JFrame("Sokoban MVC - Command Pattern");
        frame.setJMenuBar(menuBar);
        frame.add(cardPane);

        frame.setSize(WindowConstants.getWindowSize());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void showErrorDialog(int dialogType) {
        String message = "";

        switch (dialogType) {
            case MessageType.ERROR_PLAY_ZONE_IS_NULL:
                message = "Play Zone is null!";
                break;
            case MessageType.ERROR_TOO_MANY_OR_NO_PLAYER:
                message = "There is too many or no players in play zone!";
                break;
            case MessageType.ERROR_UNMATCH_BOXES_AND_GOALS:
                message = "There is different number of boxes and goals!";
                break;
            case MessageType.ERROR_NO_BOXES_OR_GOALS:
                message = "There is no boxes or goals!";
                break;
            case MessageType.ERROR_CONNECTION:
                message = "Connection error";
                break;
            default:
                return;
        }

        JOptionPane.showMessageDialog(frame, message);
    }

    public void showCard(String cardName) {
        CardLayout cardLayout = (CardLayout) cardPane.getLayout();
        cardLayout.show(cardPane, cardName);

        if (cardName.equals("GAME")) {
            frame.add(statusPanel, BorderLayout.SOUTH);
            canvas.removeKeyListener(controller);
            canvas.addKeyListener(controller);
            canvas.requestFocusInWindow();

            disablableItems.forEach((item) -> {
                item.setEnabled(true);
            });
        } else {
            frame.remove(statusPanel);
            canvas.removeKeyListener(controller);

            disablableItems.forEach((item) -> {
                item.setEnabled(false);
            });
        }
    }

    public void update() {
        canvas.repaint();
    }

    public void setTextToStatusPanel(Integer levelNumber) {
        setTextToStatusPanel("", levelNumber);
    }

    public void setTextToStatusPanel(String gameType, Integer levelNumber) {
        if (currentLevelNumber == null) {
            return;
        }
        currentLevelNumber.setText(gameType + " LEVEL: " + levelNumber);
    }

    public void dispose() {
        frame.setVisible(false);
        frame.dispose();
        System.exit(0);
    }

    private JMenuBar createJMenuBar(Controller controller, Font font) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileJMenu = createGameJMenu(controller, font);
        JMenu levelsJMenu = createLevelJMenu(controller, font);
        // JMenu editMenu = createEditMenu(controller, font);

        menuBar.add(fileJMenu);
        menuBar.add(levelsJMenu);
        // menuBar.add(editMenu);

        return menuBar;
    }

    private JMenu createGameJMenu(Controller controller, Font font) {
        JMenu menu = new JMenu("Game");

        String itemName = "New Game";
        String commandName = "NewGameCommand";
        JMenuItem newGameJMenuItem = createJMenuItem(itemName, commandName, controller);

        itemName = "Restart";
        commandName = "RestartCommand";
        JMenuItem restarJMenuItem = createJMenuItem(itemName, commandName, controller);

        itemName = "To Start Menu";
        commandName = "ToStartMenuCommand";
        JMenuItem toStartMenuItem = createJMenuItem(itemName, commandName, controller);

        itemName = "Close";
        commandName = "CloseCommand";
        JMenuItem closeJMenuItem = createJMenuItem(itemName, commandName, controller);

        itemName = "Settings";
        commandName = "SettingCommand";
        JMenuItem settingJMenuItem = createJMenuItem(itemName,commandName,controller);

        menu.setFont(font);
        newGameJMenuItem.setFont(font);
        restarJMenuItem.setFont(font);
        toStartMenuItem.setFont(font);
        closeJMenuItem.setFont(font);
        settingJMenuItem.setFont(font);

        disablableItems.add(newGameJMenuItem);
        disablableItems.add(restarJMenuItem);
        disablableItems.add(toStartMenuItem);

        menu.add(newGameJMenuItem);
        menu.add(restarJMenuItem);
        menu.add(toStartMenuItem);
        JSeparator separator = new JSeparator();
        menu.add(separator);
        menu.add(settingJMenuItem);
        separator = new JSeparator();
        menu.add(separator);
        menu.add(closeJMenuItem);

        return menu;
    }

    private JMenu createLevelJMenu(Controller controller, Font font) {
        JMenu menu = new JMenu("Levels");

        final int LEVELS = controller.getModel().getLevelsRepository().getLevelsCounter();
        for (int i = 1; i <= LEVELS; i++) {
            String itemName = "Level " + i;
            String commandName = "Level" + i + "Command";
            JMenuItem item = createJMenuItem(itemName, commandName, controller);
            item.setFont(font);
            disablableItems.add(item);
            menu.add(item);
        }

        menu.setFont(font);

        return menu;
    }

    private JMenuItem createJMenuItem(String itemName, String command, Controller controller) {
        JMenuItem item = new JMenuItem(itemName);
        item.addActionListener(controller);
        item.setActionCommand(command);

        return item;
    }

    private JPanel createCardJPanel(Controller controller, Font font) {
        final int FONT_SIZE = 50;
        font = font.deriveFont((float) FONT_SIZE);

        JPanel startPane = createStartCard(controller, font);

        JPanel singlePlayerPane = createSinglePlauerCard(controller, font);
        JPanel multiplayerPane = createMultiplayerMenuCard(controller, font);

        JPanel winLevelPane = createWinLevelCard("YOU WIN", controller, font);
        JPanel winGamePane = createWinGameCard("YOU WIN", controller, font);

        JPanel playerOneWinLevelCard = createWinLevelCard("PLAYER 1 WIN LEVEL", controller, font);
        JPanel playerTwoWinLevelCard = createWinLevelCard("PLAYER 2 WIN LEVEL", controller, font);
        JPanel playerOneWinGameCard = createWinGameCard("PLAYER 1 WIN GAME", controller, font);
        JPanel playerTwoWinGameCard = createWinGameCard("PLAYER 2 WIN GAME", controller, font);
        JPanel drawCard = createWinGameCard("ITS DRAW", controller, font);
        
        JPanel playerOneClientWinLevelWaitCard = createClientWaitCard("YOU WIN LEVEL", controller, font);        
        JPanel playerTwoClientWinLevelWaitCard = createClientWaitCard("YOU LOSE LEVEL", controller, font);
        JPanel playerOneClientWinGameWaitCard = createClientWaitCard("YOU WIN GAME", controller, font);
        JPanel playerTwoClientWinGameWaitCard = createClientWaitCard("YOU LOSE GAME", controller, font);
        JPanel playerOneClientDrawWaitCard = createClientWaitCard("ITS DRAW", controller, font);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPane = new JPanel(cardLayout);
        cardPane.add(startPane, "START");

        cardPane.add(singlePlayerPane, "SINGLEPLAYER");
        cardPane.add(multiplayerPane, "MULTIPLAYER");

        cardPane.add(canvas, "GAME");

        cardPane.add(winLevelPane, "WIN_LEVEL");
        cardPane.add(winGamePane, "WIN_GAME");

        cardPane.add(playerOneWinLevelCard, "PLAYER_ONE_WIN_LEVEL");
        cardPane.add(playerTwoWinLevelCard, "PLAYER_TWO_WIN_LEVEL");
        cardPane.add(playerOneWinGameCard, "PLAYER_ONE_WIN_GAME");
        cardPane.add(playerTwoWinGameCard, "PLAYER_TWO_WIN_GAME");
        cardPane.add(drawCard, "DRAW");

        cardPane.add(playerOneClientWinLevelWaitCard, "CLIENT_PLAYER_ONE_WIN_LEVEL");
        cardPane.add(playerTwoClientWinLevelWaitCard, "CLIENT_PLAYER_TWO_WIN_LEVEL");
        cardPane.add(playerOneClientWinGameWaitCard, "CLIENT_PLAYER_ONE_WIN_GAME");
        cardPane.add(playerTwoClientWinGameWaitCard, "CLIENT_PLAYER_TWO_WIN_GAME");
        cardPane.add(playerOneClientDrawWaitCard, "CLIENT_DRAW");

        return cardPane;
    }

    private JPanel createStartCard(Controller controller, Font font) {
        JLabel title = new JLabel("SOKOBAN MVC", SwingConstants.CENTER);
        JButton singleplayer = new JButton("SINGLE PLAYER");
        JButton multiplayer = new JButton("MULTIPLAYER");
        JButton quit = new JButton("QUIT");

        String actionCommand = "MenuSingleplayerCommand";
        singleplayer.setActionCommand(actionCommand);
        singleplayer.addActionListener(controller);

        actionCommand = "MenuMultiplayerCommand";
        multiplayer.setActionCommand(actionCommand);
        multiplayer.addActionListener(controller);

        actionCommand = "CloseCommand";
        quit.setActionCommand(actionCommand);
        quit.addActionListener(controller);

        title.setFont(font);
        singleplayer.setFont(font);
        multiplayer.setFont(font);
        quit.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        title.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        singleplayer.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        multiplayer.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        quit.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(title);
        pane.add(singleplayer);
        pane.add(multiplayer);
        pane.add(quit);

        return pane;
    }

    private JPanel createSinglePlauerCard(Controller controller, Font font) {
        JLabel title = new JLabel("CHOOSE", SwingConstants.CENTER);
        JButton onePlayer = new JButton("ONE PLAYER");
        JButton twoPlayers = new JButton("TWO PLAYERS");
        JButton cancel = new JButton("CANCEL");

        String actionCommand = "GameStartOnePlayerCommand";
        onePlayer.setActionCommand(actionCommand);
        onePlayer.addActionListener(controller);

        actionCommand = "GameStartTwoPlayerCommand";
        twoPlayers.setActionCommand(actionCommand);
        twoPlayers.addActionListener(controller);

        actionCommand = "ToStartMenuCommand";
        cancel.setActionCommand(actionCommand);
        cancel.addActionListener(controller);

        title.setFont(font);
        onePlayer.setFont(font);
        twoPlayers.setFont(font);
        cancel.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        title.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        onePlayer.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        twoPlayers.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        cancel.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(title);
        pane.add(onePlayer);
        pane.add(twoPlayers);
        pane.add(cancel);

        return pane;
    }

    private JPanel createMultiplayerMenuCard(Controller controller, Font font) {
        JLabel title = new JLabel("CHOOSE", SwingConstants.CENTER);
        JButton server = new JButton("SERVER");
        JButton client = new JButton("CLIENT");
        JButton cancel = new JButton("CANCEL");

        String actionCommand = "GameStartAsServerCommand";
        server.setActionCommand(actionCommand);
        server.addActionListener(controller);

        actionCommand = "GameStartAsClientCommand";
        client.setActionCommand(actionCommand);
        client.addActionListener(controller);

        actionCommand = "ToStartMenuCommand";
        cancel.setActionCommand(actionCommand);
        cancel.addActionListener(controller);

        title.setFont(font);
        server.setFont(font);
        client.setFont(font);
        cancel.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        title.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        server.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        client.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        cancel.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(title);
        pane.add(server);
        pane.add(client);
        pane.add(cancel);

        return pane;
    }

    private JPanel createWinLevelCard(String title, Controller controller, Font font) {
        JLabel titleJLabel = new JLabel(title, SwingConstants.CENTER);
        JButton nextLevel = new JButton("NEXT");
        JButton restartLevel = new JButton("RESTART");
        JButton toStartMenu = new JButton("TO START MENU");

        String actionCommand = "NextCommand";
        nextLevel.setActionCommand(actionCommand);
        nextLevel.addActionListener(controller);

        actionCommand = "RestartCommand";
        restartLevel.setActionCommand(actionCommand);
        restartLevel.addActionListener(controller);

        actionCommand = "ToStartMenuCommand";
        toStartMenu.setActionCommand(actionCommand);
        toStartMenu.addActionListener(controller);

        titleJLabel.setFont(font);
        nextLevel.setFont(font);
        restartLevel.setFont(font);
        toStartMenu.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        titleJLabel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        nextLevel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        restartLevel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        toStartMenu.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(titleJLabel);
        pane.add(nextLevel);
        pane.add(restartLevel);
        pane.add(toStartMenu);

        return pane;
    }

    private JPanel createWinGameCard(String title, Controller controller, Font font) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        JButton restartGame = new JButton("RESTART GAME");
        JButton toStartMenu = new JButton("TO START MENU");

        String actionCommand = "NewGameCommand";
        restartGame.setActionCommand(actionCommand);
        restartGame.addActionListener(controller);

        actionCommand = "ToStartMenuCommand";
        toStartMenu.setActionCommand(actionCommand);
        toStartMenu.addActionListener(controller);

        titleLabel.setFont(font);
        restartGame.setFont(font);
        toStartMenu.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        titleLabel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        restartGame.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        toStartMenu.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(titleLabel);
        pane.add(restartGame);
        pane.add(toStartMenu);

        return pane;
    }

    private JPanel createClientWaitCard(String title, Controller controller, Font font) {
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        JLabel waitLabel = new JLabel("WAITING SERVER", SwingConstants.CENTER);
        JButton toStartMenu = new JButton("TO START MENU");
        
        String actionCommand = "ToStartMenuCommand";
        toStartMenu.setActionCommand(actionCommand);
        toStartMenu.addActionListener(controller);
        
        titleLabel.setFont(font);
        waitLabel.setFont(font);
        toStartMenu.setFont(font);

        final int WIDTH = WindowConstants.WINDOW_WIDTH / 2;
        final int HEIGTH = 80;
        final int SPACING = 20;
        int x = (WindowConstants.WINDOW_WIDTH - WIDTH) / 2;
        int y = 50;
        titleLabel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        waitLabel.setBounds(x, y, WIDTH, HEIGTH);
        y = y + HEIGTH + SPACING;
        toStartMenu.setBounds(x, y, WIDTH, HEIGTH);

        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(titleLabel);
        pane.add(waitLabel);
        pane.add(toStartMenu);

        return pane;
    }

    public static final class WindowConstants {

        public static final int WINDOW_WIDTH = 1000;
        public static final int WINDOW_HEIGTH = 600;

        private static final Dimension WINDOW_SIZE = new Dimension(WINDOW_WIDTH, WINDOW_HEIGTH);

        private static Dimension getWindowSize() {
            return WINDOW_SIZE;
        }

        private WindowConstants() {
        }
    }

    public static final class MessageType {

        public static final int ERROR_PLAY_ZONE_IS_NULL         = 1;
        public static final int ERROR_TOO_MANY_OR_NO_PLAYER     = 3;
        public static final int ERROR_UNMATCH_BOXES_AND_GOALS   = 4;
        public static final int ERROR_NO_BOXES_OR_GOALS         = 5;
        public static final int ERROR_CONNECTION                = 6;

        private MessageType() {
        }
    }
}
