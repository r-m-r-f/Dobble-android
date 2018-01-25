package dobbleproject.dobble.Server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.HandClearedPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.SocketWrapper;

public class ServerGameSocketReader extends Thread {
    private BufferedReader in;
    private int playerNumber;
    private Handler uiHandler;
    private SocketWrapper readerSocket;

    private Packet packet;

    private boolean isRunning;

    public ServerGameSocketReader(int playerNumber, Handler uiHandler) {
        this.playerNumber = playerNumber;
        this.uiHandler = uiHandler;
    }
    @Override
    public void run() {
        readerSocket = ServerPlayersList.getPlayer(playerNumber).getReaderSocket();
        in = readerSocket.getReader();

        isRunning = true;
        Log.d("server listener", "startted listening");

        while (isRunning && !interrupted()) {
            try {
                String response = in.readLine();
                packet = PacketParser.getPacketFromString(response);

                Class packetClass = packet.getClass();
                Log.d("server listener", "got class" + packetClass.getName());

                if(packetClass == SelectedPicturePacket.class) {
                    int cardIndex = ((SelectedPicturePacket)packet).getCardIndex();
                    int pictureIndex = ((SelectedPicturePacket) packet).getPictureIndex();

                    Log.d("server,selected pic", Integer.toString(cardIndex) + " : " + Integer.toString(pictureIndex));

                    Bundle bundle = new Bundle();
                    bundle.putInt("number", playerNumber);
                    bundle.putInt("card", cardIndex);
                    bundle.putInt("picture", pictureIndex);

                    Message message = new Message();
                    message.what = MessageType.SELECTED_PICTURE;
                    message.setData(bundle);

                    uiHandler.sendMessage(message);

                }
                else if(packetClass == HandClearedPacket.class) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("number", playerNumber);

                    Message message = new Message();
                    message.what = MessageType.HAND_CLEARED;
                    message.setData(bundle);

                    uiHandler.sendMessage(message);
                }
            } catch (IOException e) {
                isRunning = false;
            }


        }


    }
}
