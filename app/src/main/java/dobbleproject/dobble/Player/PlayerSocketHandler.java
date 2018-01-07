package dobbleproject.dobble.Player;

import java.io.IOException;
import java.net.Socket;

/*
 */

public class PlayerSocketHandler {
    private static Socket socket = null;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket) throws IOException {
        if(PlayerSocketHandler.socket != null) {
            PlayerSocketHandler.socket.close();
        }
        PlayerSocketHandler.socket = socket;
    }

    public static int getPort() {
        return socket.getLocalPort();
    }
}
