import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Connection extends Thread {

    public static final int START_RECEIVING = 100;
    public static final int INIT_GAME = 101;
    public static final int UPDATE_GAME = 102;

    private ActionListener listener;

    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private volatile DataWrapper receivedData;
    private boolean server;
    private boolean exit;

    // run as server
    private Connection(ActionListener listener, int port) throws IOException {
        this(listener);

        serverSocket = new ServerSocket(port);
        final int SECOND = 1000;
        final int MINUTE = 60 * SECOND;
        serverSocket.setSoTimeout(MINUTE);
        socket = serverSocket.accept();
        
        in = socket.getInputStream();
        out = socket.getOutputStream();

        server = true;
    }

    // run as client
    private Connection(ActionListener listener, String host, int port) throws IOException {
        this(listener);

        socket = new Socket(host, port);
        
        in = socket.getInputStream();
        out = socket.getOutputStream();

        server = false;
    }

    private Connection(ActionListener listener) {
        this.listener = listener;
        serverSocket = null;
        socket = null;
        in = null;
        out = null;
        receivedData = null;
        exit = false;
    }

    public static Connection getClientConnection(ActionListener listener, String host, int port) throws IOException {
        return new Connection(listener, host, port);
    }

    public static Connection getServerConnection(ActionListener listener, int port) throws IOException {
        return new Connection(listener, port);
    }

    public void run() {
        try {
            int data = in.read();
            while ((! exit) || data != -1) {
                if (data == START_RECEIVING) {
                    data = in.read();
                    if (data == INIT_GAME) {
                        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "InitGameCommand");
                        listener.actionPerformed(event);
                    } else if (data == UPDATE_GAME) {
                        receivedData = receiveChanges();
                        
                        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "UpdateModelFromServerCommand");
                        listener.actionPerformed(event);
                    }
                }

                data = in.read();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        } finally {
            closeConnection();
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_FIRST, "ConnectionClosedCommand");
            listener.actionPerformed(event);
        }
    }

    public void sendMessage(int message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public DataWrapper receiveChanges() throws IOException {
        int data = in.read();
        int[][] receivedPlayZone = new int[data][];
        for (int i = 0; i < receivedPlayZone.length; i++) {
            data = in.read();
            receivedPlayZone[i] = new int[data];
            for (int j = 0; j < receivedPlayZone[i].length; j++) {
                data = in.read();
                receivedPlayZone[i][j] = data;
            }
        }
        int receivedPlayerDirection = in.read();
        int receivedPlayerX = in.read();
        int receivedPlayerY = in.read();

        int receivedMatchedGoalsCounter = in.read();
        int receivedPlayerWins = in.read();

        DataWrapper resivedData = new DataWrapper(receivedPlayZone, 
                                                  receivedPlayerDirection, 
                                                  receivedPlayerX, 
                                                  receivedPlayerY, 
                                                  receivedMatchedGoalsCounter, 
                                                  receivedPlayerWins);
    
        return resivedData;
    }

    public void sendChanges(DataWrapper sendingData) throws IOException {
        int[][] playZone = sendingData.getPlayZone();
        int playerDirection = sendingData.getPlayerDirection();
        int playerX = sendingData.getPlayerX();
        int playerY = sendingData.getPlayerY();

        int matchedGoalsCounter = sendingData.getMatchedGoalsCounter();
        int playerWins = sendingData.getPlayerWinsCounter();

        out.write(playZone.length);
        for (int i = 0; i < playZone.length; i++) {
            out.write(playZone[i].length);
            for (int j = 0; j < playZone[i].length; j++) {
                out.write(playZone[i][j]);
            }
        }

        out.write(playerDirection);
        out.write(playerX);
        out.write(playerY);

        out.write(matchedGoalsCounter);
        out.write(playerWins);

        out.flush();
    }

    public void closeConnection() {
        exit = true;
        try {
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isServer() {
        return server;
    }

    public DataWrapper getReceivedData() {
        return receivedData;
    }
}
