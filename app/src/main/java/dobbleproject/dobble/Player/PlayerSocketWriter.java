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
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.SocketWrapper;

public class PlayerSocketWriter extends Thread {
    private SocketWrapper socket = null;
    private int playerNumber;
    private Handler mHandler;

    BufferedWriter in;

    private  boolean isRunning = false;

    @SuppressLint("HandlerLeak")
    public PlayerSocketWriter(SocketWrapper socket, int playerNumber) {
        this.socket = socket;
        this.playerNumber = playerNumber;

        in = socket.getWriter();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageType.SELECTED_PICTURE:
                        Bundle b = msg.getData();
                        SelectedPicturePacket packet = new SelectedPicturePacket(b.getInt("card"), b.getInt("picture"));

                        Log.d("selected pic", Integer.toString(b.getInt("card")) + " : " + Integer.toString(b.getInt("picture")));

                        try {
                            // TODO: Refactor
                            in.write(packet.toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                }
            }
        };
    }

    public synchronized Handler getHandler() {
        return mHandler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        isRunning = true;


        Looper.prepare();
        Looper.loop();
    }
}

//public class PlayerSocketWriter extends Handler {
//
//    private SocketWrapper socket = null;
//    private int playerNumber;
//
//
//    public PlayerSocketWriter(SocketWrapper socket, int playerNumber) {
//        this.socket = socket;
//        this.playerNumber = playerNumber;
//
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//        super.handleMessage(msg);
//    }
//}
