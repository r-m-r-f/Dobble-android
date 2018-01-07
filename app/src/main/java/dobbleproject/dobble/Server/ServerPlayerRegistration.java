package dobbleproject.dobble.Server;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import dobbleproject.dobble.MessageHelper;
import dobbleproject.dobble.MessageType;

public class ServerPlayerRegistration extends Thread {
    private DatagramSocket listenerSocket = null;
    private DatagramSocket senderSocket = null;

//    Socket serverSocket = null;

    private Handler uiHandler;

    String serverName;
    String serverIp;

    private static int numberOfPlayers;
    private static int registered;

    boolean isRunning = true;

    public ServerPlayerRegistration(String serverName, String serverIp, int numberOfPlayers, Handler uiHandler) {
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.numberOfPlayers = numberOfPlayers;
        this.uiHandler = uiHandler;
    }


    @Override
    public void run() {
        try {
            ServerPlayersList.clearPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerSocket ss = ServerSocketSingleton.getServerSocket();

        while (isRunning && registered < numberOfPlayers) {
            try {
                Socket playerSocket = ss.accept();

                // TODO: Change Player to Socket
                ServerPlayersList.addPlayer(new Player(null, playerSocket));
                registered++;

                uiHandler.sendMessage(MessageHelper.createDebugMessage("registered " + playerSocket.getInetAddress()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Message message = new Message();
        message.what = MessageType.PLAYERS_LIST_READY;

        uiHandler.sendMessage(message);
    }

    public void quit() {
        interrupt();
        isRunning = false;
        if(listenerSocket != null) {
            listenerSocket.close();
        }
        uiHandler.sendMessage(MessageHelper.createDebugMessage("registration stopped"));
    }

}
