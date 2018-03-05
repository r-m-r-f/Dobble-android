package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectedPicturePacket extends Packet {
    private int cardIndex;
    private int pictureIndex;

    public SelectedPicturePacket(int cardIndex, int pictureIndex) {
        this.cardIndex = cardIndex;
        this.pictureIndex = pictureIndex;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public int getPictureIndex() {
        return pictureIndex;
    }

    @Override
    protected JSONObject createPayload() throws JSONException {
        JSONObject payload = new JSONObject();

        payload.put("type", "selected");
        payload.put("card", cardIndex);
        payload.put("picture", pictureIndex);

        return payload;
    }
}
