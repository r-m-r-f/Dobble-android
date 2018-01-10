package dobbleproject.dobble.Player;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import dobbleproject.dobble.AppConfiguration;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.AcknowledgementPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.RegisterAcceptedPacket;
import dobbleproject.dobble.Packet.RegisterRequestPacket;

public class PlayerRegisterRequest extends Thread {
    private Handler uiHandler;
    private boolean isRunning = true;


    // TODO: Send player name to the server
    private String playerName;
    private String playerIp;
    private String serverIp;
    private int serverPort;

    private Socket playerSocket = null;

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
            playerSocket = PlayerSocketHandler.getSocket();
            InetAddress address = InetAddress.getByName(serverIp);
            playerSocket.connect(new InetSocketAddress(address,serverPort), AppConfiguration.SOCKET_TIMEOUT);

            message = new Message();
            message.what = MessageType.PLAYER_REGISTERED;

            Log.d("register", "registered!");
        } catch (IOException e) {
            message.what = MessageType.REGISTER_REQUEST_ERROR;
            e.printStackTrace();
        } finally {
            uiHandler.sendMessage(message);
        }
    }

    public synchronized void quit() {
        
    }
}
