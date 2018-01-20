package dobbleproject.dobble.Server;

import java.net.ServerSocket;

public class ServerSocketSingleton {
    private static ServerSocket serverSocket = null;

    public static synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static synchronized void setServerSocket(ServerSocket serverSocket) {
        ServerSocketSingleton.serverSocket = serverSocket;
    }

    public static synchronized int getPort() {
        return serverSocket.getLocalPort();
    }
}
