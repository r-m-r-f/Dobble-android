package dobbleproject.dobble;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientThread extends GameJobThread {

    private String clientName;

    // TODO: Create a class for server info
    private InetAddress serverAddress;
    private int serverPort;
    private String serverName;

    WifiManager.MulticastLock multicastLock;

    public ClientThread(Context uiContext, Handler uiHandler, String name) {
        super(uiContext, uiHandler);
        this.clientName = name;
    }

    @Override
    public void run() {
        try {
            // Check if wifi is enabled
            if (!WifiHelper.isUpAndRunning(wifiManager)) {
                // Send an error message and return
                msg = createErrorMessage("Wifi is disabled!");
                uiHandler.sendMessage(msg);
                return;
            }

            ip = WifiHelper.getIpAddress(wifiManager);
            broadcastAddress = WifiHelper.getBroadcastAddress(wifiManager);

            listenerSocket = new DatagramSocket(LISTENER_PORT);
            listenerPort = listenerSocket.getLocalPort();

            // Enable receiving broadcasted packets
            WifiManager.WifiLock lock = wifiManager.createWifiLock("mylock");
            lock.acquire();
            multicastLock = wifiManager.createMulticastLock("stackoverflow for the win");
            multicastLock.acquire();

            // Packet to receive
            // TODO: Refactor packet creation
            byte[] bytes = new byte[MAX_PACKET_LENGTH];
            DatagramPacket announcementPacket = new DatagramPacket(bytes, MAX_PACKET_LENGTH);

            while(!Thread.interrupted()) {
                listenerSocket.receive(announcementPacket);

                try {
                    JSONObject payload = new JSONObject(new String(announcementPacket.getData()));

                    // Check packet type, not needed for now
                    if(payload.getString("type").equals("announce")) {
                        serverName = payload.getString("serverName");
                        serverPort = payload.getInt("port");
                        // Remove '/' for the address
                        serverAddress = InetAddress.getByName(payload.getString("ip").replaceFirst("/",""));
                        uiHandler.sendMessage(createErrorMessage(serverName + ", " + serverAddress.getHostAddress()));
                    }
                } catch (JSONException e) {
                    // Do nothing
                }
                sleep(100);
            }
        }  catch (InterruptedException e) {
            // Clean up if thread is interrupted
            listenerSocket.close();
            uiHandler.sendMessage(createErrorMessage("Client has stopped"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
