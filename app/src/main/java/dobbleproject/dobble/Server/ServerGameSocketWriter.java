package dobbleproject.dobble.Server;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.ConfirmSelectionPacket;
import dobbleproject.dobble.Packet.EndGamePacket;
import dobbleproject.dobble.Packet.GameSetupPacket;
import dobbleproject.dobble.Packet.NewHandPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.Packet.StartGamePacket;
import dobbleproject.dobble.Packet.WrongSelectionPacket;
import dobbleproject.dobble.SocketWrapper;


public class ServerGameSocketWriter {

    private HandlerThread thread;
    private SocketWrapper socket = null;
    private Handler mHandler;

    BufferedWriter out;

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg){
            Packet packet = null;
            Bundle b;
            Log.d("in server writer", Integer.toString(msg.what));
            switch (msg.what) {
                case MessageType.GAME_SETUP:
                    b = msg.getData();
                    packet = new GameSetupPacket(b.getStringArrayList("players"), b.getInt("number"));
                    break;
                case MessageType.NEW_GAME:
                    packet = new StartGamePacket();
                    break;
                case MessageType.NEW_HAND:
                    b = msg.getData();
                    packet = new NewHandPacket(b.getIntegerArrayList("hand"));
                    break;
                case MessageType.CONFIRMED_SELECTION:
                    packet = new ConfirmSelectionPacket();
                    break;
                case MessageType.WRONG_SELECTION:
                    packet = new WrongSelectionPacket();
                    break;
                case MessageType.END_GAME:
                    packet = new EndGamePacket(msg.getData().getInt("winner"));
                    break;
            }

            try {
                if(packet != null && socket != null && !socket.isClosed()) {
                    Log.d("in server writer", "trying to write " + packet.toString());
                    out.write(packet.toString());
                    out.flush();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    };

    @SuppressLint("HandlerLeak")
    public ServerGameSocketWriter(SocketWrapper socket) {
        this.socket = socket;
        out = socket.getWriter();

        thread = new HandlerThread("serversocketwriter");
    }

    public synchronized Handler getHandler() {
        return mHandler;
    }

    public void start() {
        thread.start();
        mHandler = new Handler(thread.getLooper(), callback);
    }

    public void quit() throws IOException {
        Boolean result = null;
//        if (out != null) {
//            out.close();
//        }
        thread.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        if (thread.isAlive())
            result = thread.quit();

        Log.d("server socket writer", "thread quit: " + (result!=null?Boolean.toString(result): "null"));
    }

}