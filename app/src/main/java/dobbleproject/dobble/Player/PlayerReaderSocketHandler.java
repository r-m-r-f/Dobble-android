package dobbleproject.dobble.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import dobbleproject.dobble.SocketWrapper;

public class PlayerReaderSocketHandler {
    private static ServerSocket serverSocket = null;

    private static SocketWrapper socket = null;

    public synchronized static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized static void setServerSocket(ServerSocket serverSocket) throws IOException {
        if(PlayerReaderSocketHandler.serverSocket != null) {
            PlayerReaderSocketHandler.serverSocket.close();
        }
        PlayerReaderSocketHandler.serverSocket = serverSocket;
    }

    public static SocketWrapper getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) throws IOException {
        if(PlayerReaderSocketHandler.socket != null) {
            PlayerReaderSocketHandler.socket.close();
        }
        PlayerReaderSocketHandler.socket = new SocketWrapper(socket);
    }

    public static void close() throws IOException {
        if( PlayerReaderSocketHandler.socket != null) {
            PlayerReaderSocketHandler.socket.close();
        }

        if (PlayerReaderSocketHandler.serverSocket != null) {
            PlayerReaderSocketHandler.serverSocket.close();
        }
    }


}
