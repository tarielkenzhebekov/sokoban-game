import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Canvas extends JPanel {

    private Model model;
    private Image imageBlank;
    private Image imageWall;
    private Image imageBox;
    private Image imageGoal;
    private Image imageBackground;
    private Map<Integer, Image> imagePlayerDirectionMap;

    public Canvas(Model model, String style) {
        this.model = model;
        loadImagesPack(style);

        setOpaque(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        final int tileWindth = 40;
        final int tileHeigth = 40;
        if (model.isTwoPlayerGame()) {
            drawTwoPlayerGame(g, tileWindth, tileHeigth);
        } else {
            drawOnePlayerGame(g, tileWindth, tileHeigth);
        }
    }

    private void loadImagesPack(String style) {
        try {
            String directory = "images/";
            if ("NewYear".equals(style)) {
                directory = "images/newyear/";
            } else if ("Default".equals(style)) {
                directory = "images/default/";
            } else if ("Grinch".equals(style)) {
                directory = "images/grinch/";
            }

            File filePlayerLeft = new File(directory + "player_left.png");
            File filePlayerUp = new File(directory + "player_up.png");
            File filePlayerRight = new File(directory + "player_right.png");
            File filePlayerDown = new File(directory + "player_down.png");

            Image imagePlayerLeft = ImageIO.read(filePlayerLeft);
            Image imagePlayerUp = ImageIO.read(filePlayerUp);
            Image imagePlayerRight = ImageIO.read(filePlayerRight);
            Image imagePlayerDown = ImageIO.read(filePlayerDown);

            imagePlayerDirectionMap = new HashMap<>();
            imagePlayerDirectionMap.put(Model.Direction.UP, imagePlayerUp);
            imagePlayerDirectionMap.put(Model.Direction.DOWN, imagePlayerDown);
            imagePlayerDirectionMap.put(Model.Direction.LEFT, imagePlayerLeft);
            imagePlayerDirectionMap.put(Model.Direction.RIGHT, imagePlayerRight);

            File fileBlank = new File(directory + "blank.png");
            File fileWall = new File(directory + "wall.png");
            File fileBox = new File(directory + "box.png");
            File fileGoal = new File(directory + "goal.png");

            imageBlank = ImageIO.read(fileBlank);
            imageWall = ImageIO.read(fileWall);
            imageBox = ImageIO.read(fileBox);
            imageGoal = ImageIO.read(fileGoal);
            imageBackground = new ImageIcon(directory + "background.gif").getImage();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private void drawOnePlayerGame(Graphics g, int tileWindth, int tileHeigth) {
        int[][] playZone = model.getPlayerOnePlayZone();

        final int PLAY_ZONE_WIDTH = playZone[0].length * tileWindth;
        final int PLAY_ZONE_HEIGTH = playZone.length * tileHeigth;

        final int START_X = (Viewer.WindowConstants.WINDOW_WIDTH - PLAY_ZONE_WIDTH) / 2;
        final int START_Y = ((Viewer.WindowConstants.WINDOW_HEIGTH - PLAY_ZONE_HEIGTH) / 2) - 50;

        g.drawImage(imageBackground, 0, 0, getWidth(), getHeight(), this);

        drawPlayZone(g, playZone, tileWindth, tileHeigth, START_X, START_Y);

        Movable player = model.getPlayerOne();
        drawPlayer(g, player, tileWindth, tileHeigth, START_X, START_Y);
    }

    private synchronized void drawTwoPlayerGame(Graphics g, int tileWindth, int tileHeigth) {
        int[][] playZone1 = model.getPlayerOnePlayZone();
        int[][] playZone2 = model.getPlayerTwoPlayZone();

        final int PLAY_ZONE_WIDTH = playZone1[0].length * tileWindth;
        final int PLAY_ZONE_HEIGTH = playZone1.length * tileHeigth;

        final int WINDOW_WIDTH = Viewer.WindowConstants.WINDOW_WIDTH;
        final int WINDOW_HEIGTH = Viewer.WindowConstants.WINDOW_HEIGTH;

        final int START_Y = ((WINDOW_HEIGTH - PLAY_ZONE_HEIGTH) / 2) - 50;

        final int START_X1 = ((WINDOW_WIDTH / 2) - PLAY_ZONE_WIDTH) / 2;
        final int START_X2 = (((WINDOW_WIDTH / 2) - PLAY_ZONE_WIDTH) / 2)
                             + ((WINDOW_WIDTH - START_X1) / 2);
        
        g.drawImage(imageBackground, 0, 0, getWidth(), getHeight(), this);

        drawPlayZone(g, playZone1, tileWindth, tileHeigth, START_X1, START_Y);
        drawPlayZone(g, playZone2, tileWindth, tileHeigth, START_X2, START_Y);

        Movable player = model.getPlayerOne();
        drawPlayer(g, player, tileWindth, tileHeigth, START_X1, START_Y);
        player = model.getPlayerTwo();
        drawPlayer(g, player, tileWindth, tileHeigth, START_X2, START_Y);
    }
    private void drawPlayZone(Graphics g, int[][] playZone, int tileWidth, int tileHeight, int xStart, int yStart) {

        int x = xStart;
        int y = yStart;

        if (playZone == null) {
            return;
        }


        for (int i = 0; i < playZone.length; i++) {
            for (int j = 0; j < playZone[i].length; j++) {
                int element = playZone[i][j];

                if (element != Model.GameElement.WALL && !isWithinWalls(j, i, playZone)) {
                    continue;
                }
                if (element == Model.GameElement.BLANK || element == Model.GameElement.PLAYER) {
                    g.drawImage(imageBlank, x, y, tileWidth, tileHeight, null);
                } else if (element == Model.GameElement.BOX) {
                    g.drawImage(imageBox, x, y, tileWidth, tileHeight, null);
                } else if (element == Model.GameElement.GOAL) {
                    g.drawImage(imageGoal, x, y, tileWidth, tileHeight, null);
                } else if (element == Model.GameElement.WALL) {
                    g.drawImage(imageWall, x, y, tileWidth, tileHeight, null);
                }

                x = x + tileWidth;
            }
            x = xStart;
            y = y + tileHeight;
        }
    }

    private boolean isWithinWalls(int x, int y, int[][] playZone) {
        // Check if the coordinates are within the boundaries defined by the walls
        return x >= 0 && x < playZone[0].length && y >= 0 && y < playZone.length && playZone[y][x] != Model.GameElement.WALL;
    }

    private void drawPlayer(Graphics g, Movable player,
            int tileWindth, int tileHeigth, int playZoneXStart, int playeZoneYStart) {

        int x = player.getX();
        int y = player.getY();
        int direction = player.getDirection();

        x = playZoneXStart + x * tileWindth;
        y = playeZoneYStart + y * tileWindth;
        Image imagePlayer = imagePlayerDirectionMap.get(direction);
        g.drawImage(imagePlayer, x, y, tileWindth, tileHeigth, null);
    }
}
