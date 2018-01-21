package dobbleproject.dobble.Packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dobbleproject.dobble.Game.Card;

public class NewTurnPacket extends Packet {
    Card card;

    public NewTurnPacket(Card card) {
        this.card = card;
    }

    public NewTurnPacket(JSONArray json) {
        this.card = new Card(json);
    }

    public Card getCard() {
        return card;
    }


    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload
                .put("type", "newTurn")
                .put("card", card.toJson());

        return payload;
    }
}
