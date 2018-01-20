package dobbleproject.dobble.Packet;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dobbleproject.dobble.AppConfiguration;

public abstract class Packet  {

    protected byte[] toBytes(JSONObject payload) {
        return payload.toString().getBytes();
    }

    protected DatagramPacket createDatagram(JSONObject payload, String destinationIp) throws JSONException, UnknownHostException {
        byte[] bytes = toBytes(payload);
        InetAddress address = InetAddress.getByName(destinationIp);
        return new DatagramPacket(bytes, bytes.length, address, AppConfiguration.LISTENER_PORT);
    }

    public DatagramPacket getDatagram(String destinationIp) throws JSONException, UnknownHostException {
        return createDatagram(createPayload(), destinationIp);
    }

    public JSONObject getPayload() {
        try {
            return createPayload();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract JSONObject createPayload() throws JSONException;
}
