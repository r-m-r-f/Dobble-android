package dobbleproject.dobble.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.GameSetupPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Server.Player;

public class PlayerSocketReader extends Thread {
    private BufferedReader in;
    private Handler uiHandler;
    private Socket playerSocket;

    private Packet packet;

    private boolean isRunning = true;

    public PlayerSocketReader(Handler uiHandler) {
        this.uiHandler = uiHandler;

        playerSocket = PlayerSocketHandler.getSocket();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));

            while (isRunning && !interrupted()) {
                String response = in.readLine();
                packet = PacketParser.getPacketFromString(response);

                if(packet instanceof GameSetupPacket) {
                    ArrayList<Card> hand = ((GameSetupPacket) packet).getHand();

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("hand", hand);

                    Message msg = new Message();
                    msg.what = MessageType.HAND_DELIVERED;
                    msg.setData(bundle);
                    uiHandler.sendMessage(msg);
                }

                // TODO: Handle game logic

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }



    }
}
