package dobbleproject.dobble.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import dobbleproject.dobble.AppConfiguration;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.AnnouncementPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Server.ServerInfo;

public class PlayerServerDiscovery extends Thread {
    private DatagramSocket listenerSocket;
//    Handler uiHandler;
    WeakReference<Handler> uiHandler;
    private boolean isRunning = true;

    public PlayerServerDiscovery(Handler uiHandler) {
        this.uiHandler = new WeakReference<>(uiHandler);
    }

    @Override
    public void run() {

        byte[] buffer = new byte[AppConfiguration.MAX_PACKET_LENGTH];
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        Packet response;

        try {
            listenerSocket = new DatagramSocket(AppConfiguration.ANNOUNCEMENT_LISTENER_PORT, InetAddress.getByName("0.0.0.0"));
            listenerSocket.setBroadcast(true);
            Log.d("PlayerServerDiscovery", "created a socket");
        } catch (SocketException e) {
            Log.d("PlayerServerDiscovery", "can't create a socket");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while(!isInterrupted() && isRunning) {
            try {
                listenerSocket.receive(datagram);

                Log.d("PlayerServerDiscovery", new String(datagram.getData()));

                response = PacketParser.getPacketFromDatagram(datagram);


                if(response instanceof AnnouncementPacket) {
                    ServerInfo serverInfo = new ServerInfo(((AnnouncementPacket) response).getServerName(),
                            ((AnnouncementPacket) response).getServerIp(), ((AnnouncementPacket) response).getServerPort());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("info", serverInfo);

                    Message msg = new Message();
                    msg.what = MessageType.SERVER_DISCOVERED;
                    msg.setData(bundle);

                    // TODO: Probably unsafe
                    if(uiHandler.get() != null) {
                        uiHandler.get().sendMessage(msg);
                    }
                }

            } catch (IOException e) {
                Log.d("PlayerServerDiscovery", "closed socket");
            }
        }
        Log.d("server discovery", "thread quits!");
    }

    public void quit() {
        interrupt();
        isRunning = false;

        if(listenerSocket != null) {
            listenerSocket.close();
        }
    }
}
