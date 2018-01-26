package dobbleproject.dobble.Server;

import android.os.Handler;
import org.json.JSONException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;

import dobbleproject.dobble.MessageHelper;
import dobbleproject.dobble.Packet.AnnouncementPacket;

public class ServerAnnouncement extends Thread {
    DatagramSocket broadcastSocket = null;
    String broadcastAddress;
    String serverName;
    String serverIp;

    Handler uiHandler;

    boolean isRunning = true;

    public ServerAnnouncement(String serverName, String serverIp, String broadcastAddress, Handler uiHandler) {
        this.broadcastAddress = broadcastAddress;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.uiHandler = uiHandler;
    }


    @Override
    public void run() {
        DatagramPacket announcementPacket;

        try {
            broadcastSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        uiHandler.sendMessage(MessageHelper.createDebugMessage("Broadcast"));

        while(!isInterrupted() && isRunning) {
            try {
                announcementPacket = new AnnouncementPacket(serverName, serverIp, ServerSocketSingleton.getPort()).getDatagram(broadcastAddress);
                broadcastSocket.send(announcementPacket);
                uiHandler.sendMessage(MessageHelper.createDebugMessage("sent announcement"));
                sleep(2000);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO: Move isRunning to quit()
                isRunning = false;
            }
        }
        uiHandler.sendMessage(MessageHelper.createDebugMessage("server announcement stopped"));
    }


    public void quit() {
        interrupt();
        isRunning = false;

        if(broadcastSocket != null) {
            broadcastSocket.close();
        }
    }
}
