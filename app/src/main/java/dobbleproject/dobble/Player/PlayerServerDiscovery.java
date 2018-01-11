package dobbleproject.dobble.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import dobbleproject.dobble.AppConfiguration;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.AnnouncementPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Server.ServerInfo;

public class PlayerServerDiscovery extends Thread {
    private DatagramSocket listenerSocket;
    Handler uiHandler;
    private boolean isRunning = true;

    public PlayerServerDiscovery(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @Override
    public void run() {

        byte[] buffer = new byte[AppConfiguration.MAX_PACKET_LENGTH];
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        Packet response;

        try {
            listenerSocket = new DatagramSocket(AppConfiguration.LISTENER_PORT);
        } catch (SocketException e) {
            Log.e("PlayerServerDiscovery", "can't create a socket");
            e.printStackTrace();
        }

        while(!isInterrupted() && isRunning) {
            try {
                listenerSocket.receive(datagram);
                response = PacketParser.getPacket(datagram);

                if(response instanceof AnnouncementPacket) {
                    ServerInfo serverInfo = new ServerInfo(((AnnouncementPacket) response).getServerName(),
                            ((AnnouncementPacket) response).getServerIp(), ((AnnouncementPacket) response).getServerPort());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("info", serverInfo);

                    Message msg = new Message();
                    msg.what = MessageType.SERVER_DISCOVERED;
                    msg.setData(bundle);

                    uiHandler.sendMessage(msg);
                }

            } catch (IOException e) {
                Log.d("PlayerServerDiscovery", "closed socket");
            }
        }
    }

    public void quit() {
        interrupt();
        isRunning = false;

        if(listenerSocket != null) {
            listenerSocket.close();
        }
    }
}
