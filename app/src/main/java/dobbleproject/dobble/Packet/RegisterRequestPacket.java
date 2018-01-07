package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public final class RegisterRequestPacket extends Packet {
    private String playerName;
    private String playerIp;
    private int port;

    public RegisterRequestPacket(String playerName, String playerIp, int port) {
        this.playerName = playerName;
        this.playerIp = playerIp;
        //this.port = port;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public int getPort() {
        return port;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "register");
        payload.put("name", playerName);
        payload.put("ip", playerIp);
        payload.put("port", port);

        return payload;
    }
}
