package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class HandClearedPacket extends Packet {
    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "handCleared");
        return payload;
    }
}
