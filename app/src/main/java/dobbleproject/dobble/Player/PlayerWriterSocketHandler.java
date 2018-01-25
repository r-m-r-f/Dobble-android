package dobbleproject.dobble.Player;

import java.io.IOException;
import java.net.Socket;

import dobbleproject.dobble.SocketWrapper;

/*
 */

public class PlayerWriterSocketHandler {
    private static SocketWrapper socket = null;

    public static synchronized SocketWrapper getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket) throws IOException {
        if(PlayerWriterSocketHandler.socket != null) {
            PlayerWriterSocketHandler.socket.close();
        }
        PlayerWriterSocketHandler.socket = new SocketWrapper(socket);
    }

    public synchronized static int getPort() {
        return socket.getLocalPort();
    }

    public static synchronized void close() throws IOException {
        if(socket != null) {
            socket.close();
        }
    }
}
