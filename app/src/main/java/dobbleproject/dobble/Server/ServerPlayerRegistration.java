package dobbleproject.dobble.Server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
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

    private WeakReference<Handler> uiHandler;

    String serverName;
    String serverIp;

    private int numberOfPlayers;
    private int registered;

    boolean isRunning = true;

    public ServerPlayerRegistration(String serverName, String serverIp, int numberOfPlayers, Handler uiHandler) {
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.numberOfPlayers = numberOfPlayers;
        this.uiHandler = new WeakReference<Handler>(uiHandler);
    }


    @Override
    public void run() {
        try {
            ServerPlayersList.clearPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerSocket ss = ServerSocketSingleton.getServerSocket();
        if(ss == null)
            throw new RuntimeException();

        while (!isInterrupted() && registered < numberOfPlayers) {
            try {
                Log.d("player registration", ss.toString());
                Socket s = ss.accept();
                SocketWrapper readerSocket = new SocketWrapper(s);

                Log.d("registered: ", readerSocket.getInetAddress().toString());

                BufferedReader in = readerSocket.getReader();
                String message = in.readLine();
                Packet packet = PacketParser.getPacketFromString(message);

                Log.d("packet type ", packet.getClass().toString());

                if(packet instanceof RegisterRequestPacket) {
                    // Create writer socket
                    String playerIp = ((RegisterRequestPacket) packet).getPlayerIp();
                    int playerPort = ((RegisterRequestPacket) packet).getPort();
                    Socket writerSocket = new Socket(playerIp, playerPort);

                    ServerPlayersList.addPlayer(new Player(new PlayerInfo(((RegisterRequestPacket) packet).getPlayerName(),
                            ((RegisterRequestPacket) packet).getPlayerIp(), ((RegisterRequestPacket) packet).getPort()), readerSocket, new SocketWrapper(writerSocket)));
                    registered++;
                }

                {
                    Handler handler = uiHandler.get();
                    if (handler != null) {
                        handler.sendMessage(MessageHelper.createDebugMessage("registered " + readerSocket.getInetAddress()));
                    }
                    handler = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Message message = new Message();
        message.what = MessageType.PLAYERS_LIST_READY;

        {
            Handler handler = uiHandler.get();
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
    }

    public void quit() {
        interrupt();
        isRunning = false;
        if(listenerSocket != null) {
            listenerSocket.close();
        }

        {
            Handler handler = uiHandler.get();
            if (handler != null) {
                handler.sendMessage(MessageHelper.createDebugMessage("registration stopped"));
            }
        }
    }

}
