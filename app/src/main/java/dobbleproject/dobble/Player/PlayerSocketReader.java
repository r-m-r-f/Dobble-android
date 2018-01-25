package dobbleproject.dobble.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
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
    private Handler uiHandler;
    private SocketWrapper playerSocket;

    private Packet packet;

    private boolean isRunning = true;

    public PlayerSocketReader(Handler uiHandler) {
        this.uiHandler = uiHandler;

        playerSocket = PlayerReaderSocketHandler.getSocket();
    }

    @Override
    public void run() {
        try {
            in = playerSocket.getReader();

            while (isRunning && !interrupted()) {
                String response = in.readLine();
                Log.d("player reader", "response: " + response);
                packet = PacketParser.getPacketFromString(response);

                Class packetClass = packet.getClass();

                // TODO: Refactor
                if(packetClass == GameSetupPacket.class) {
                    ArrayList<String> playersNames = ((GameSetupPacket) packet).getPlayerNames();
                    int playerNumber = ((GameSetupPacket) packet).getPlayerNumber();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("names", playersNames);
                    bundle.putInt("playerNumber", playerNumber);

                    Message msg = new Message();
                    msg.what = MessageType.GAME_SETUP;
                    msg.setData(bundle);
                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == StartGamePacket.class){
                    Message msg = new Message();
                    msg.what = MessageType.NEW_GAME;
                    uiHandler.sendMessage(msg);

                }
                else if(packetClass == NewHandPacket.class) {
                    ArrayList<Integer> handCardsIndexes = ((NewHandPacket) packet).getCardsIndexes();

                    Bundle bundle = new Bundle();
                    bundle.putIntegerArrayList("hand", handCardsIndexes);

                    Message msg = new Message();
                    msg.what = MessageType.HAND_DELIVERED;
                    msg.setData(bundle);
                    uiHandler.sendMessage(msg);
                }
                else if(packetClass == NewTurnPacket.class) {
                    Card card = ((NewTurnPacket) packet).getCard();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("card", card);

                    Message msg = new Message();
                    msg.what = MessageType.NEW_TURN;
                    msg.setData(bundle);
                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == ConfirmSelectionPacket.class) {
                    Message msg = new Message();
                    msg.what = MessageType.CONFIRMED_SELECTION;
                    uiHandler.sendMessage(msg);
                }
                else if(packetClass == WrongSelectionPacket.class) {
                    Message msg = new Message();
                    msg.what = MessageType.WRONG_SELECTION;
                    uiHandler.sendMessage(msg);
                }
                else if (packetClass == EndGamePacket.class) {
                    Message msg = new Message();
                    msg.what = MessageType.END_GAME;

                    Bundle b = new Bundle();
                    b.putInt("winner", ((EndGamePacket) packet).getWinner());

                    msg.setData(b);
                    uiHandler.sendMessage(msg);
                }

                // TODO: Handle game logic

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        Log.d("player socket reader", "quits");
    }

    public synchronized void quit() {
        interrupt();
    }
}
