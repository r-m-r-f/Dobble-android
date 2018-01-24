package dobbleproject.dobble.Server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.SelectedPicturePacket;
import dobbleproject.dobble.SocketWrapper;

public class ServerGameSocketListener extends Thread {
    private BufferedReader in;
    private int playerNumber;
    private Handler uiHandler;
    private SocketWrapper playerSocket;

    private Packet packet;

    private boolean isRunning;

    public ServerGameSocketListener(int playerNumber, Handler uiHandler) {
        this.playerNumber = playerNumber;
        this.uiHandler = uiHandler;
    }
    @Override
    public void run() {
        playerSocket = ServerPlayersList.getPlayer(playerNumber).getReaderSocket();
        in = playerSocket.getReader();

        isRunning = true;

        while (isRunning && !interrupted()) {
            try {
                Log.d("server listener", "startted listening");
                String response = in.readLine();
                packet = PacketParser.getPacketFromString(response);

                Class packetClass = packet.getClass();

                if(packetClass == SelectedPicturePacket.class) {
                    int cardIndex = ((SelectedPicturePacket)packet).getCardIndex();
                    int pictureIndex = ((SelectedPicturePacket) packet).getCardIndex();

                    Log.d("selected pic", Integer.toString(cardIndex) + " : " + Integer.toString(pictureIndex));

                    Bundle bundle = new Bundle();
                    bundle.putInt("card", cardIndex);
                    bundle.putInt("picture", pictureIndex);

                    Message message = new Message();
                    message.what = MessageType.SELECTED_PICTURE;
                    message.setData(bundle);

                    uiHandler.sendMessage(message);

                }
            } catch (IOException e) {
                isRunning = false;
            }


        }


    }
}
