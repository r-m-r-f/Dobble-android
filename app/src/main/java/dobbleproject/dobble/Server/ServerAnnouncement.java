package dobbleproject.dobble.Server;

import android.os.Handler;
import org.json.JSONException;
import java.io.IOException;
import java.lang.ref.WeakReference;
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

    private WeakReference<Handler> uiHandler;

    boolean isRunning = true;

    public ServerAnnouncement(String serverName, String serverIp, String broadcastAddress, Handler uiHandler) {
        this.broadcastAddress = broadcastAddress;
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.uiHandler = new WeakReference<>(uiHandler);
    }


    @Override
    public void run() {
        DatagramPacket announcementPacket;

        try {
            broadcastSocket = new DatagramSocket();
            broadcastSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // TODO: Remove scoping if it doesn't help
        {
            Handler handler = uiHandler.get();
            if (handler != null) {
                handler.sendMessage(MessageHelper.createDebugMessage("Broadcast"));
            }
            handler = null;
        }

        while(!isInterrupted() && isRunning) {
            try {
                announcementPacket = new AnnouncementPacket(serverName, serverIp, ServerSocketSingleton.getPort()).getDatagram(broadcastAddress);
                broadcastSocket.send(announcementPacket);

                // TODO: Remove scoping if it doesn't help
                {
                    Handler handler = uiHandler.get();
                    if (handler != null) {
                        handler.sendMessage(MessageHelper.createDebugMessage("sent announcement"));
                    }
                    handler = null;
                }

//                uiHandler.sendMessage(MessageHelper.createDebugMessage("sent announcement"));
                Thread.currentThread().sleep(2000);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO: Move isRunning to quit()
                isRunning = false;
            }
        }

        // TODO: Remove scoping if it doesn't help
        {
            Handler handler = uiHandler.get();
            if (handler != null) {
                handler.sendMessage(MessageHelper.createDebugMessage("server announcement stopped"));
            }
        }
    }


    public void quit() {
        interrupt();
        isRunning = false;

        if(broadcastSocket != null) {
            broadcastSocket.close();
        }
    }
}
