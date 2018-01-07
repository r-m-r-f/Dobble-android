package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterAcceptedPacket extends Packet {
    private String serverName;
    private String serverIp;
    private int serverPort;

    public RegisterAcceptedPacket(String serverName, String serverIp, int serverPort) {
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "accepted");
        payload.put("name", serverName);
        payload.put("ip", serverIp);
        payload.put("port", serverPort);

        return payload;
    }
}
