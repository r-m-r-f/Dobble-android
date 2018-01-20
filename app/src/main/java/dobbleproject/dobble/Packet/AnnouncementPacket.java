package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dobbleproject.dobble.AppConfiguration;

public final class AnnouncementPacket extends Packet {

    // TODO: Refactor later
    private String serverName;
    private String serverIp;
    private int serverSocketPort;

    public AnnouncementPacket(String serverName, String serverIp, int serverSocketPort) {
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.serverSocketPort = serverSocketPort;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverSocketPort;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "announce");
        payload.put("name", serverName);
        payload.put("ip", serverIp);
        payload.put("port", serverSocketPort);

        return payload;
    }

}
