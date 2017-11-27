package dobbleproject.dobble;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class GameJobThread extends Thread {

    protected final int MAX_PACKET_LENGTH = 500;
    protected final int LISTENER_PORT = 18888;
    //public static GameJobThread gameJobThread;
    // Debug tag
    protected final String TAG = this.getName();

    // UI
    protected Context uiContext;
    protected Handler uiHandler;

    // Reusable message
    protected Message msg;

    // Network info
    protected WifiManager wifiManager;
    protected InetAddress ip;
    protected InetAddress broadcastAddress;

    // Socket info
    protected DatagramSocket listenerSocket;
    protected int listenerPort;

//    GameJobThread() {}

    protected GameJobThread(Context uiContext, Handler uiHandler) {
        this.uiContext = uiContext;
        this.uiHandler = uiHandler;

        wifiManager = (WifiManager) uiContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void run() {

    }

    public void endJob() {
        this.interrupt();
    }

    protected Message createErrorMessage(String text) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg", text);
        msg.what = ServerMessage.ERROR;
        msg.setData(bundle);
        return msg;
    }
}
