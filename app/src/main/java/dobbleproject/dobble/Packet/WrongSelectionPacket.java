package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import dobbleproject.dobble.Packet.Packet;

public class WrongSelectionPacket extends Packet {
    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "penalty");
        return payload;
    }
}
