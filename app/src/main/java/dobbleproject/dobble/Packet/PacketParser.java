package dobbleproject.dobble.Packet;

import org.json.JSONObject;

import java.net.DatagramPacket;

public class PacketParser {
    public static Packet getPacket(DatagramPacket datagram) {
        Packet packet = null;
        try {
            JSONObject payload = new JSONObject(new String(datagram.getData()));

            switch (payload.getString("type")) {
                case "announce":
                    packet = new AnnouncementPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "register":
                    packet = new RegisterRequestPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "ack":
                    packet = new AcknowledgementPacket();
                    break;
                case "accepted":
                    packet = new RegisterAcceptedPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

}
