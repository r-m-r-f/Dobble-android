package dobbleproject.dobble.Packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewHandPacket extends Packet {
    private ArrayList<Integer> cardsIndexes = new ArrayList<>();

    public NewHandPacket(ArrayList<Integer> cardsIndexes) {
        this.cardsIndexes = cardsIndexes;
    }

    public NewHandPacket(JSONArray cardsIndexes) {
        try {
            for(int i=0; i < cardsIndexes.length(); i++) {
                this.cardsIndexes.add(cardsIndexes.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getCardsIndexes() {
        return cardsIndexes;
    }


    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        JSONArray cardsIndexesJson = new JSONArray(cardsIndexes);

        payload.put("type", "hand");
        payload.put("hand", cardsIndexesJson);
        return payload;
    }
}
