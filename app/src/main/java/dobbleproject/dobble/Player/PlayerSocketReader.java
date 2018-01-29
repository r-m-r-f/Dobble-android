package dobbleproject.dobble.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.util.ArrayList;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.MessageType;
import dobbleproject.dobble.Packet.ConfirmSelectionPacket;
import dobbleproject.dobble.Packet.EndGamePacket;
import dobbleproject.dobble.Packet.GameSetupPacket;
import dobbleproject.dobble.Packet.NewHandPacket;
import dobbleproject.dobble.Packet.NewTurnPacket;
import dobbleproject.dobble.Packet.Packet;
import dobbleproject.dobble.Packet.PacketParser;
import dobbleproject.dobble.Packet.WrongSelectionPacket;
import dobbleproject.dobble.Packet.StartGamePacket;
import dobbleproject.dobble.SocketWrapper;

public class PlayerSocketReader extends Thread {
    private BufferedReader in;
    private final WeakReference<Handler> uiHandler;

    // TODO: Consider using WeakReference
    private SocketWrapper playerSocket;

    private Packet packet = null;

    private boolean isRunning = true;

    public PlayerSocketReader(Handler uiHandler) {
        this.uiHandler = new WeakReference<>(uiHandler);

        playerSocket = PlayerReaderSocketHandler.getSocket();
    }

    @Override
    public void run() {
        try {
            in = playerSocket.getReader();

            Message msg = null;
            while (isRunning && !interrupted()) {
                String response = in.readLine();
                if (response != null) {
                    Log.d("player reader", "response: " + response);
                    packet = PacketParser.getPacketFromString(response);
                }

                Class packetClass = null;
                if (packet != null) {
                    packetClass = packet.getClass();
                }
                // TODO: Refactor
                if(packetClass == GameSetupPacket.class) {
                    ArrayList<String> playersNames = ((GameSetupPacket) packet).getPlayerNames();
                    int playerNumber = ((GameSetupPacket) packet).getPlayerNumber();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("names", playersNames);
                    bundle.putInt("playerNumber", playerNumber);

                    msg = new Message();
                    msg.what = MessageType.GAME_SETUP;
                    msg.setData(bundle);
//                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == StartGamePacket.class){
                    msg = new Message();
                    msg.what = MessageType.NEW_GAME;
//                    uiHandler.sendMessage(msg);

                }
                else if(packetClass == NewHandPacket.class) {
                    ArrayList<Integer> handCardsIndexes = ((NewHandPacket) packet).getCardsIndexes();

                    Bundle bundle = new Bundle();
                    bundle.putIntegerArrayList("hand", handCardsIndexes);

                    msg = new Message();
                    msg.what = MessageType.HAND_DELIVERED;
                    msg.setData(bundle);
//                    uiHandler.sendMessage(msg);
                }
                else if(packetClass == NewTurnPacket.class) {
                    Card card = ((NewTurnPacket) packet).getCard();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("card", card);

                    msg = new Message();
                    msg.what = MessageType.NEW_TURN;
                    msg.setData(bundle);
//                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == ConfirmSelectionPacket.class) {
                    msg = new Message();
                    msg.what = MessageType.CONFIRMED_SELECTION;
//                    uiHandler.sendMessage(msg);
                }
                else if(packetClass == WrongSelectionPacket.class) {
                    msg = new Message();
                    msg.what = MessageType.WRONG_SELECTION;
//                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == EndGamePacket.class) {
                    msg = new Message();
                    msg.what = MessageType.END_GAME;

                    Bundle b = new Bundle();
                    b.putInt("winner", ((EndGamePacket) packet).getWinner());

                    msg.setData(b);
//                    uiHandler.sendMessage(msg);
                }

                if(msg != null && uiHandler.get() !=null) {
                    uiHandler.get().sendMessage(msg);
                    msg = null;
                }

                // TODO: Handle game logic

            }
        } catch (SocketException e) {
            isRunning = false;
            Log.d("player socket reader", "socket exception");
        } catch (IOException e) {
            isRunning = false;
            e.printStackTrace();
        }

        Log.d("player socket reader", "quits");
    }

    public void quit() {
        if(in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isAlive())
            interrupt();
        isRunning = false;
    }
}
