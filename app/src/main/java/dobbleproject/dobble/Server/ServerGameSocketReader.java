package dobbleproject.dobble.Server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;

import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.HandClearedPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.PlayerReadyPacket;
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.SocketWrapper;

public class ServerGameSocketReader extends Thread {
    private BufferedReader in;
    private int playerNumber;
    private WeakReference<Handler> uiHandler;
    private SocketWrapper readerSocket;

    private Packet packet = null;

    private boolean isRunning;

    public ServerGameSocketReader(int playerNumber, Handler uiHandler) {
        this.playerNumber = playerNumber;
        this.uiHandler = new WeakReference<Handler>(uiHandler);
    }
    @Override
    public void run() {
        readerSocket = ServerPlayersList.getPlayer(playerNumber).getReaderSocket();
        in = readerSocket.getReader();

        isRunning = true;
        Log.d("server listener", "started listening");

        Message message = null;
        String response = null;
        while (isRunning && !interrupted()) {
            try {
                if (!readerSocket.isClosed()) {
                    response = in.readLine();
                }
                if (response != null) {
                    packet = PacketParser.getPacketFromString(response);
                    response = null;
                }

                Class packetClass = null;
                if (packet != null) {
                    packetClass = packet.getClass();
                }
                Log.d("server listener", "got class" + packetClass.getName());

                if(packetClass == SelectedPicturePacket.class) {
                    int cardIndex = ((SelectedPicturePacket)packet).getCardIndex();
                    int pictureIndex = ((SelectedPicturePacket) packet).getPictureIndex();

                    Log.d("server,selected pic", Integer.toString(cardIndex) + " : " + Integer.toString(pictureIndex));

                    Bundle bundle = new Bundle();
                    bundle.putInt("number", playerNumber);
                    bundle.putInt("card", cardIndex);
                    bundle.putInt("picture", pictureIndex);

                    message = new Message();
                    message.what = MessageType.SELECTED_PICTURE;
                    message.setData(bundle);
                }
                else if(packetClass == HandClearedPacket.class) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("number", playerNumber);

                    message = new Message();
                    message.what = MessageType.HAND_CLEARED;
                    message.setData(bundle);
                }
                else if (packetClass == PlayerReadyPacket.class) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("number", ((PlayerReadyPacket) packet).getPlayerNumber());

                    Log.d("server socket reader", "index" + Integer.toString(((PlayerReadyPacket) packet).getPlayerNumber()));

                    message = new Message();
                    message.what = MessageType.PLAYER_READY;
                    message.setData(bundle);
                }

                if (message != null) {
                    // Get handler
                    Handler handler = uiHandler.get();
                    if (handler != null) {
                        handler.sendMessage(message);
                    }
                }

            } catch (SocketException e) {
                isRunning = false;
                Log.d("server socket reader", "exception, thread hard quits");
                return;
            } catch (IOException e) {
                isRunning = false;
                Log.d("server socket reader", "exception, thread hard quits");
                return;
            }
        }
        Log.d("server socket reader", "thread quits");
    }

    public void quit() throws IOException {
        if (in != null) {
            in.close();
        }
        if (isAlive())
            interrupt();
        isRunning = false;
    }
}
