import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLevel extends Level {

    private static final int KB = 1024;

    private Path path;

    public FileLevel(String path) {
        this(Paths.get(path));
    }

    public FileLevel(Path path) {
        this.path = path;
    }

    @Override
    public int[][] getPlayZone() {
        String data = readDataFromFile();

        int[][] playZone = parseData(data, '\n');

        return playZone;
    }

    private String readDataFromFile() {
        StringBuilder result = new StringBuilder();

        try {
            FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);

            ByteBuffer buffer = ByteBuffer.allocate(KB * 8);
            while (channel.position() < channel.size()) {
                readToBuffer(channel, buffer);
                result.append(Charset.forName("UTF-8").decode(buffer));
                buffer.flip();
            }

            channel.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        return result.toString();
    }

    private void readToBuffer(FileChannel channel, ByteBuffer buffer) throws IOException {
        do {
            int bytes = channel.read(buffer);

            if (bytes <= 0) {
                break;
            }
        } while (buffer.hasRemaining());

        buffer.flip();
    }
}
