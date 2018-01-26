package dobbleproject.dobble.Player;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;

import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.HandClearedPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PlayerReadyPacket;
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.SocketWrapper;

public class PlayerSocketWriter {

    private HandlerThread thread;
    private SocketWrapper socket = null;
    private int playerNumber;
    private Handler mHandler;

    BufferedWriter out;

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg){
            Packet packet = null;
            switch (msg.what) {
                case MessageType.PLAYER_READY:
                    Log.d("player socket writer", Integer.toString(msg.getData().getInt("number")));
                    packet = new PlayerReadyPacket(msg.getData().getInt("number"));
                    break;
                case MessageType.SELECTED_PICTURE:
                    Bundle b = msg.getData();
                    packet = new SelectedPicturePacket(b.getInt("card"), b.getInt("picture"));

                    Log.d("thread, selected pic", Integer.toString(b.getInt("card")) + " : " + Integer.toString(b.getInt("picture")));
                    break;
                case MessageType.HAND_CLEARED:
                    packet = new HandClearedPacket();
                    break;
            }

            try {
                // TODO: Refactor
                Log.d("player socket writer", packet.toString());
                out.write(packet.toString());
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
    };

    @SuppressLint("HandlerLeak")
    public PlayerSocketWriter(SocketWrapper socket, int playerNumber) {
        this.socket = socket;
        this.playerNumber = playerNumber;

        out = socket.getWriter();

        thread = new HandlerThread("playersocketwriter"+playerNumber);
    }

    public synchronized Handler getHandler() {
        return mHandler;
    }

    public void start() {
        thread.start();
        mHandler = new Handler(thread.getLooper(), callback);
    }

    public void quit() {
        Boolean result = null;
        thread.interrupt();
        if (thread.isAlive())
             result = thread.quitSafely();

        Log.d("player socket writer", "thread quit: " + Boolean.toString(result));
    }

}