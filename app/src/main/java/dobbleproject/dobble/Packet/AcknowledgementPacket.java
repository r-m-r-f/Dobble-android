package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class AcknowledgementPacket extends Packet {
    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "ack");
        // TODO: Check if more fields are needed

        return payload;
    }
}
