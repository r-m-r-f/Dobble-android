package dobbleproject.dobble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class ServerThread extends GameJobThread {

    // Server serverName
    String serverName;

    // Broadcast socket
    private DatagramSocket broadcastSocket;

    // TODO: Implement a class for client info
    // List of connected clients, currently stores addresses
    private List<InetAddress> clients;



    public ServerThread(Context uiContext, Handler uiHandler, String serverName) {
        super(uiContext, uiHandler);
        this.serverName = serverName;
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

            // Setup sockets
            broadcastSocket = new DatagramSocket();
            int broadcastPort = broadcastSocket.getLocalPort();

            listenerSocket = new DatagramSocket(LISTENER_PORT);
            listenerPort = listenerSocket.getLocalPort();

            // Create an announcement packet
            DatagramPacket announcementPacket;
            JSONObject jsonPayload = new JSONObject();

            jsonPayload.put("type", "announce");
            jsonPayload.put("serverName", serverName);
            jsonPayload.put("ip", ip);
            jsonPayload.put("port", listenerPort);
            byte[] jsonBytes = jsonPayload.toString().getBytes();

            announcementPacket = new DatagramPacket(jsonBytes, jsonBytes.length, broadcastAddress, LISTENER_PORT);

            uiHandler.sendMessage(createErrorMessage("Broadcast"));

            // TODO: App doesn't handle disconnections
            while (!Thread.interrupted()) {
                // Send only if device is connected
                if(WifiHelper.isUpAndRunning(wifiManager)) {
                    // Announce server in the network
                    broadcastSocket.send(announcementPacket);
                    uiHandler.sendMessage(createErrorMessage("Sent a packet"));
                } else {
                    uiHandler.sendMessage(createErrorMessage("Can't send a packet"));
                }
                sleep(2000);
            }

        } catch (InterruptedException e) {
            // Clean up if thread is interrupted
            broadcastSocket.close();
            listenerSocket.close();
            uiHandler.sendMessage(createErrorMessage("Server has stopped"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
