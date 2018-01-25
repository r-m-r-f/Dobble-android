package dobbleproject.dobble.Packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dobbleproject.dobble.Game.Card;

public class GameSetupPacket extends Packet {
    private ArrayList<String> players = null;
    // TODO: Implement player numbers
    private int playerNumber;

    public GameSetupPacket(ArrayList<String> players, int playerNumber) {
        this.players = players;
        this.playerNumber = playerNumber;
    }

    public GameSetupPacket(JSONArray players, int playerNumber) {
        this.playerNumber = playerNumber;

        this.players = new ArrayList<>();
        for(int i = 0; i < players.length(); i++) {
            try {
                this.players.add(players.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getPlayerNames() {
        return players;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONArray handJson = new JSONArray(players);


        JSONObject payload = new JSONObject();
        payload.put("type", "setup");
        payload.put("players", handJson);
        payload.put("number", playerNumber);
        return payload;
    }
}
