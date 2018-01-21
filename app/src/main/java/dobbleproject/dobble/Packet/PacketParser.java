package dobbleproject.dobble.Packet;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;

import dobbleproject.dobble.Game.Card;

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
                case "ack":
                    packet = new AcknowledgementPacket();
                    break;
                case "accepted":
                    packet = new RegisterAcceptedPacket(payload.getString("name"), payload.getString("ip"), payload.getInt("port"));
                    break;
                case "setup":
                    // TODO: Check if casting works
//                    Log.d("hand packet: ", );
                    packet = new GameSetupPacket(payload.getJSONArray("hand"), payload.getInt("number"));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

}
