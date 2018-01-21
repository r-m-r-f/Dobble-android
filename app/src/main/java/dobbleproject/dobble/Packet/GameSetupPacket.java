package dobbleproject.dobble.Packet;

import org.json.JSONArray;
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

    public GameSetupPacket(JSONArray hand, int playerNumber) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>();

        try {
            for(int i=0; i < hand.length(); i++) {
                JSONArray card = hand.getJSONArray(i);
                this.hand.add(new Card(card));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONArray handJson = new JSONArray();
        for(Card c : hand) {
            handJson.put(c.toJson());
        }

        JSONObject payload = new JSONObject();
        payload.put("type", "setup");
        payload.put("hand", handJson);
        payload.put("number", playerNumber);
        return payload;
    }
}
