package dobbleproject.dobble.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Handler;

import dobbleproject.dobble.SocketWrapper;

public class ServerGameListener extends Thread {
    private BufferedReader in;
    private int playerNumber;
    private Handler uiHandler;
    private SocketWrapper playerSocket;

    private boolean isRunning;

    public ServerGameListener(int playerNumber, Handler uiHandler) {
        this.playerNumber = playerNumber;
        this.uiHandler = uiHandler;
    }
    @Override
    public void run() {
        playerSocket = ServerPlayersList.getPlayer(playerNumber).getSocketWrapper();
        in = playerSocket.getReader();

        while (isRunning && !interrupted()) {

        }


    }
}
