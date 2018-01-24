package dobbleproject.dobble.Server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import dobbleproject.dobble.AppConfiguration;
import dobbleproject.dobble.MessageHelper;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.RegisterAcceptedPacket;
import dobbleproject.dobble.Packet.RegisterRequestPacket;
import dobbleproject.dobble.Player.PlayerInfo;
import dobbleproject.dobble.SocketWrapper;

public class ServerPlayerRegistration extends Thread {
    private DatagramSocket listenerSocket = null;
    private DatagramSocket senderSocket = null;

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

        //Set server socket
        ServerSocket ss = null;
        try {
            ss = ServerSocketSingleton.getServerSocket();
            if(ss != null) {
                ss.close();
            }
            ss = new ServerSocket(0, numberOfPlayers);
            ServerSocketSingleton.setServerSocket(ss);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }


        while (!isInterrupted() && registered < numberOfPlayers) {
            try {
                Log.d("player registration", ss.toString());
                Socket s = ss.accept();
                SocketWrapper playerSocket = new SocketWrapper(s);

                Log.d("registered: ", playerSocket.getInetAddress().toString());

                BufferedReader in = playerSocket.getReader();
                String message = in.readLine();
                Packet packet = PacketParser.getPacketFromString(message);

                Log.d("packet type ", packet.getClass().toString());

                if(packet instanceof RegisterRequestPacket) {
                    String playerIp = ((RegisterRequestPacket) packet).getPlayerIp();
                    Socket writerSocket = new Socket(playerIp, AppConfiguration.PLAYER_LISTENER_PORT);

                    ServerPlayersList.addPlayer(new Player(new PlayerInfo(((RegisterRequestPacket) packet).getPlayerName(),
                            ((RegisterRequestPacket) packet).getPlayerIp(), -1), playerSocket, new SocketWrapper(writerSocket)));
                    registered++;
                }
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

        if(ServerSocketSingleton.getServerSocket() != null) {
            try {
                ServerSocketSingleton.getServerSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uiHandler.sendMessage(MessageHelper.createDebugMessage("registration stopped"));
    }

}
