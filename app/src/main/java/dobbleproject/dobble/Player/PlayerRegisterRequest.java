package dobbleproject.dobble.Player;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import dobbleproject.dobble.AppConfiguration;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.RegisterRequestPacket;
import dobbleproject.dobble.Server.Player;
import dobbleproject.dobble.SocketWrapper;

public class PlayerRegisterRequest extends Thread {
    private Handler uiHandler;
    private boolean isRunning = true;


    // TODO: Send player name to the server
    private String playerName;
    private String playerIp;
    private String serverIp;
    private int serverPort;

    private SocketWrapper playerWriterSocket = null;

    public PlayerRegisterRequest(String playerName, String playerIp, String serverIp, int serverPort, Handler uiHandler) {
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.uiHandler = uiHandler;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            // Create listener socket
            ServerSocket ss = new ServerSocket(0, 1);
            PlayerReaderSocketHandler.setServerSocket(ss);

            playerWriterSocket = PlayerWriterSocketHandler.getSocket();
            InetAddress address = InetAddress.getByName(serverIp);
            playerWriterSocket.connect(new InetSocketAddress(address, serverPort));

            BufferedWriter out = playerWriterSocket.getWriter();

            out.write(new RegisterRequestPacket(playerName, playerIp, ss.getLocalPort()).toString());
            out.flush();

            Socket readerSocket = ss.accept();
            PlayerReaderSocketHandler.setSocket(readerSocket);

            message = new Message();
            message.what = MessageType.PLAYER_REGISTERED;

            Log.d("register", "registered!");
        } catch (IOException e) {
            message.what = MessageType.REGISTER_REQUEST_ERROR;
            e.printStackTrace();
        }
        finally {
            uiHandler.sendMessage(message);
        }
    }

    public synchronized void quit() {
        
    }
}
