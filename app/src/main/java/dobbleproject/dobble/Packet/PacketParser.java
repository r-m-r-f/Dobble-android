package dobbleproject.dobble.Packet;

import org.json.JSONObject;

import java.net.DatagramPacket;

public class PacketParser {
    public static Packet getPacketFromDatagram(DatagramPacket datagram) {
        return parse(new String(datagram.getData()));
    }

    public static Packet getPacketFromString(String s) {
        return parse(s);
    }

    private static Packet parse(String s) {
        Packet packet = null;
        try {
            JSONObject payload = new JSONObject(s);

            switch (payload.getString("type")) {
                case "announce":
                    packet = new AnnouncementPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "register":
                    packet = new RegisterRequestPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "accepted":
                    packet = new RegisterAcceptedPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "setup":
                    packet = new GameSetupPacket(payload.getJSONArray("hand"), payload.getInt("number"));
                    break;
                case "hand":
                    packet = new NewHandPacket(payload.getJSONArray("hand"), payload.getInt("playerNumber"));
                    break;
                case "newTurn":
                    packet = new NewTurnPacket(payload.getJSONArray("card"));
                    break;
                case "start":
                    packet = new StartGamePacket();
                    break;
                case "penalty":
                    packet = new WrongSelectionPacket();
                    break;
                case "selected":
                    packet = new SelectedPicturePacket(payload.getInt("card"), payload.getInt("picture"));
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

}
