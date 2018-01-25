package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class EndGamePacket extends Packet {
    private int winnerNumber;

    public EndGamePacket(int winnerNumber) {
        this.winnerNumber = winnerNumber;
    }

    public int getWinner() {
        return winnerNumber;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "end");
        payload.put("winner", winnerNumber);

        return payload;
    }
}
