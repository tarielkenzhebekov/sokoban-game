public class Movable {

    private final int GAME_ELEMENT;
    private Sound sound;
    private int direction;
    private int x;
    private int y;

    public Movable(int GameElement, int x, int y) {
        this(GameElement, x, y, null);
    }

    public Movable(int gameElement, int x, int y, Sound sound) {
        this.GAME_ELEMENT = gameElement;
        this.sound = sound;
        direction = Model.Direction.DOWN;
        this.x = x;
        this.y = y;
    }

    public void playSound() {
        if (sound != null) {
            sound.play();
        }
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getGameElement() {
        return GAME_ELEMENT;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}