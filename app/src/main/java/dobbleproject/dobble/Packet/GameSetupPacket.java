package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dobbleproject.dobble.Game.Card;

public class GameSetupPacket extends Packet {
    private ArrayList<Card> hand = null;
    // TODO: Implement player numbers
    private int playerNumber;

    public GameSetupPacket(ArrayList<Card> hand, int playerNumber) {
        this.hand = hand;
        this.playerNumber = playerNumber;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("type", "setup");
        payload.put("hand", hand);
        payload.put("number", playerNumber);
        return null;
    }
}
