package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerReadyPacket extends Packet {
    private int playerNumber;

    public PlayerReadyPacket(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "ready");
        payload.put("number", playerNumber);
        return payload;
    }
}
