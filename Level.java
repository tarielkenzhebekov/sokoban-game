public abstract class Level {

    public abstract int[][] getPlayZone();

    protected int[][] parseData(String data, char newLineSymbol) {
        int rowsCount = 0;
        int columnsCount = 0;
        InnerQueue<Integer> columnsPerRow = new InnerQueue<>();

        int dataLength = data.length();
        for (int i = 0; i < dataLength; i++) {
            char ch = data.charAt(i);

            if (ch == newLineSymbol) {
                rowsCount = rowsCount + 1;
                columnsPerRow.add(columnsCount);
                columnsCount = 0;
            } else if (Character.isDigit(ch)) {
                columnsCount = columnsCount + 1;
            }
        }

        int[][] playZone = new int[rowsCount][0];
        playZone[0] = new int[columnsPerRow.pool()];
        int j = 0;
        int k = 0;
        for (int i = 0; i < dataLength; i++) {
            char ch = data.charAt(i);

            if (ch == newLineSymbol) {
                Integer columns = columnsPerRow.pool();

                if (columns == null) {
                    break;
                }

                playZone[++j] = new int[columns];
                k = 0;
            } else if (Character.isDigit(ch)) {
                playZone[j][k++] = Character.digit(ch, 10);
            }
        }
        columnsPerRow.clear();

        return playZone;
    }

    private class InnerQueue<E> {

        private static final int INITIAL_SIZE = 20;

        private int head;
        private int tail;
        private Object[] array;

        private InnerQueue() {
            this(INITIAL_SIZE);
        }

        private InnerQueue(int size) {
            array = new Object[size];
            head = 0;
            tail = 0;
        }

        private void add(E element) {
            if (tail >= array.length) {
                growSize();
            }
            array[tail++] = element;
        }

        @SuppressWarnings("unchecked")
        private E pool() {
            if (head <= tail) {
                return (E) array[head++];
            }

            return null;
        }

        private void growSize() {
            int newSize = array.length * 2;
            Object[] tempArray = new Object[newSize];

            int j = 0;
            for (int i = head; i < tail; i++) {
                tempArray[j++] = array[i];
            }

            array = tempArray;
            tail = tail - head;
            head = 0;
        }

        private void clear() {
            array = new Object[INITIAL_SIZE];
            tail = 0;
            head = 0;
        }
    }
}
