package dobbleproject.dobble.Player;

import java.io.IOException;
import java.net.Socket;

import dobbleproject.dobble.SocketWrapper;

/*
 */

public class PlayerSocketHandler {
    private static SocketWrapper socket = null;

    public static synchronized SocketWrapper getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket) throws IOException {
        if(PlayerSocketHandler.socket != null) {
            PlayerSocketHandler.socket.close();
        }
        PlayerSocketHandler.socket = new SocketWrapper(socket);
    }

    public static int getPort() {
        return socket.getLocalPort();
    }
}
