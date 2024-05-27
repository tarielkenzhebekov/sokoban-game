public class ByteCodeLevel extends Level {

    private int[][] playZone;

    public ByteCodeLevel(int[][] playZone) {
        this.playZone = playZone;
    }

    @Override
    public int[][] getPlayZone() {
        int[][] copy = new int[playZone.length][];

        for (int i = 0; i < playZone.length; i++) {
            copy[i] = playZone[i].clone();
        }

        return copy;
    }
}