package dobbleproject.dobble.Packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewHandPacket extends Packet {
    private ArrayList<Integer> cardsIndexes = new ArrayList<>();
    private int playerNumber;

    public NewHandPacket(ArrayList<Integer> cardsIndexes, int playerNumber) {
        this.cardsIndexes = cardsIndexes;
        this.playerNumber = playerNumber;
    }

    public NewHandPacket(JSONArray cardsIndexes, int playerNumber) {
        try {
            for(int i=0; i < cardsIndexes.length(); i++) {
                this.cardsIndexes.add(cardsIndexes.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.playerNumber = playerNumber;
    }

    public ArrayList<Integer> getCardsIndexes() {
        return cardsIndexes;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }


    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        JSONArray cardsIndexesJson = new JSONArray(cardsIndexes);

        payload.put("type", "hand");
        payload.put("hand", cardsIndexesJson);
        payload.put("playerNumber", playerNumber);
        return payload;
    }
}
