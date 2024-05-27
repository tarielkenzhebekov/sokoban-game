import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class NetLevel extends Level {

    private String host;
    private int port;
    private byte post;

    public NetLevel(String host, int port, byte post) {
        this.host = host;
        this.port = port;
        this.post = post;
    }

    @Override
    public int[][] getPlayZone() {
        String data = getDataOverNetWork(post);

        int[][] playZone = parseData(data, 'A');

        return playZone;
    }

    private String getDataOverNetWork(byte post) {
        String result = null;

        try {
            Socket socket = new Socket(host, port);

            OutputStream out = socket.getOutputStream();
            out.write(post);
            out.flush();

            InputStream in = socket.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            result = bufferedReader.readLine();

            in.close();
            out.close();
            socket.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        return result;
    }
}
