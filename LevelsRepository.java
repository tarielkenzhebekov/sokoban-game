import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class LevelsRepository {

    private int levelCounter;

    private Map<Integer, Level> levels;

    public LevelsRepository() {
        levels = new HashMap<>();
        levelCounter = 0;

        ByteCodeLevel byteCodeLevel1 = new ByteCodeLevel(new int[][] {
                { 5, 5, 2, 2, 2, 5, 5, 5 },
                { 5, 5, 2, 4, 2, 5, 5, 5 },
                { 5, 5, 2, 3, 2, 2, 2, 2 },
                { 2, 2, 2, 0, 0, 3, 4, 2 },
                { 2, 4, 3, 0, 1, 2, 2, 2 },
                { 2, 2, 2, 2, 3, 2, 5, 5 },
                { 5, 5, 5, 2, 4, 2, 5, 5 },
                { 5, 5, 5, 2, 2, 2, 5, 5 },
        });
        addLevel(byteCodeLevel1);

        ByteCodeLevel byteCodeLevel2 = new ByteCodeLevel(new int[][] {
                { 2, 2, 2, 2, 2, 5, 5, 5, 5 },
                { 2, 1, 0, 0, 2, 5, 5, 5, 5 },
                { 2, 0, 3, 3, 2, 5, 2, 2, 2 },
                { 2, 0, 3, 0, 2, 5, 2, 4, 2 },
                { 2, 2, 2, 0, 2, 2, 2, 4, 2 },
                { 5, 2, 2, 0, 0, 0, 0, 4, 2 },
                { 5, 2, 0, 0, 0, 2, 0, 0, 2 },
                { 5, 2, 0, 0, 0, 2, 2, 2, 2 },
                { 5, 2, 2, 2, 2, 2, 5, 5, 5 },
        });
        addLevel(byteCodeLevel2);

        ByteCodeLevel byteCodeLevel3 = new ByteCodeLevel(new int[][] {
                { 5, 2, 2, 2, 2, 2, 2, 2, 5, 5 },
                { 5, 2, 0, 0, 0, 0, 0, 2, 2, 2 },
                { 2, 0, 3, 2, 2, 2, 0, 0, 0, 2 },
                { 2, 0, 1, 0, 3, 0, 0, 3, 0, 2 },
                { 2, 0, 4, 4, 2, 0, 3, 0, 2, 2 },
                { 2, 2, 4, 4, 2, 0, 0, 0, 2, 5 },
                { 5, 2, 2, 2, 2, 2, 2, 2, 2, 5 },
        });
        addLevel(byteCodeLevel3);

        FileLevel fileLevel1 = new FileLevel("./levels/level4.sok");
        addLevel(fileLevel1);
        FileLevel fileLevel2 = new FileLevel("./levels/level5.sok");
        addLevel(fileLevel2);
        FileLevel fileLevel3 = new FileLevel("./levels/level6.sok");
        addLevel(fileLevel3);

        final String HOST = "127.0.0.1";
        final int PORT = 4449;
        NetLevel netLevel1 = new NetLevel(HOST, PORT, (byte) '7');
        addLevel(netLevel1);
        NetLevel netLevel2 = new NetLevel(HOST, PORT, (byte) '8');
        addLevel(netLevel2);
        NetLevel netLevel3 = new NetLevel(HOST, PORT, (byte) '9');
        addLevel(netLevel3);
    }

    public boolean removeLevel(Level level) {
        Iterator<Integer> it = levels.keySet().iterator();
        while (it.hasNext()) {
            Integer levelNumber = it.next();
            if (level == levels.get(levelNumber)) {
                levels.remove(levelNumber);
                return true;
            }
        }
        return false;
    }

    public boolean removeLevel(int levelNumber) {
        boolean result = false;
        if (levels.containsKey(levelNumber)) {
            levels.remove(levelNumber);
            result = true;
        }
        return result;
    }

    public Level getLevel(int elementNumber) {
        return levels.get(elementNumber);
    }

    public void addLevel(Level level) {
        levelCounter = levelCounter + 1;
        levels.put(levelCounter, level);
    }

    public int getLevelsCounter() {
        return levelCounter;
    }
}
