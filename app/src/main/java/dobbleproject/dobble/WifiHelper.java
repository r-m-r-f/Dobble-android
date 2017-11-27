package dobbleproject.dobble;


import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Wifi helper methods
 */
public class WifiHelper {
    /**
     *
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getIpAddress(WifiManager wifiManager) throws UnknownHostException {
        String _ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return InetAddress.getByName(_ip);
    }



    /**
     *
     * @return
     * @throws UnknownHostException
     */
    // https://www.depicus.com/blog/android-getting-the-broadcast-address-of-your-wifi-connection/
    public static InetAddress getBroadcastAddress(WifiManager wifiManager) throws UnknownHostException {
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null) {
            System.out.println("Could not get broadcast address");
            return null;
        }
        int broadcast = (dhcpInfo.ipAddress & dhcpInfo.netmask)
                | ~dhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * Checks if device completed authentication
     * @param wifiManager
     * @return
     */
    public static boolean isUpAndRunning(WifiManager wifiManager) {
        return wifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED ?
                true : false;
    }
}
