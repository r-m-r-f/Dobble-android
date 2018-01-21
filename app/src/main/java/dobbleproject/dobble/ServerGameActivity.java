package dobbleproject.dobble;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Game.Deck;
import dobbleproject.dobble.Game.Deck4;
import dobbleproject.dobble.Packet.AcknowledgementPacket;
import dobbleproject.dobble.Packet.GameSetupPacket;
import dobbleproject.dobble.Server.Player;
import dobbleproject.dobble.Server.ServerPlayersList;

public class ServerGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<ImageView> cardImages  = new ArrayList<>();

    Deck deck;
    Card currentCard;



    int numberOfPlayers;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_game);

        numberOfPlayers = getIntent().getExtras().getInt("numberOfPlayers");

        deck = new Deck4();
        Stack<Card> cards = deck.getCards();
        Collections.shuffle(cards);

        currentCard = cards.pop();

        ArrayList<ArrayList<Card>> playersHands = new ArrayList<>();

        int numberOfCardsInHand = cards.size() / numberOfPlayers;

        for(int i = 0; i < numberOfPlayers; i++ ){
            ArrayList<Card> hand = new ArrayList<>();
            for(int j = 0; j < numberOfCardsInHand; j++) {
                hand.add(cards.pop());
            }
            playersHands.add(hand);
        }

        // Send hands to players, probably will block ui
        new SendHandThread(playersHands).start();


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    private void setImagesFromResources(){
        images.add(R.drawable.banana);
        images.add(R.drawable.budzik);
        images.add(R.drawable.dom);
        images.add(R.drawable.drzewo);
        images.add(R.drawable.korona);
        images.add(R.drawable.ksiezyc);
        images.add(R.drawable.kwiat);
        images.add(R.drawable.malpa);
        images.add(R.drawable.oksy);
        images.add(R.drawable.shrek);
        images.add(R.drawable.skrzypce);
        images.add(R.drawable.szpilki);
        images.add(R.drawable.york);
    }

    private void setCardImages(){
        cardImages.add((ImageView)findViewById(R.id.image));
        cardImages.add((ImageView)findViewById(R.id.image1));
        cardImages.add((ImageView)findViewById(R.id.image2));
        cardImages.add((ImageView)findViewById(R.id.image3));
    }

    class SendHandThread extends Thread {
        ArrayList<ArrayList<Card>> hands;

        public SendHandThread(ArrayList<ArrayList<Card>> hands) {
            this.hands = hands;
        }

        @Override
        public void run() {
            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
                SocketWrapper s = ServerPlayersList.getPlayer(i).getSocketWrapper();
                try {
                    sleep(400);
                    BufferedWriter out = s.getWriter();

                    GameSetupPacket packet = new GameSetupPacket(hands.get(i), i);

                    // Test connection
                    //AcknowledgementPacket packet = new AcknowledgementPacket();

                    out.write(packet.toString());
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

//    class SendHandTask extends AsyncTask<ArrayList<ArrayList<Card>>, Void, Void> {
//
//        @Override
//        protected Void doInBackground(ArrayList<ArrayList<Card>>[] arrayLists) {
//            ArrayList<ArrayList<Card>> hands = arrayLists[0];
//
//            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
//                SocketWrapper s = ServerPlayersList.getPlayer(i).getSocketWrapper();
//                try {
//                    BufferedWriter out = s.getWriter();
//
//                    //GameSetupPacket packet = new GameSetupPacket(hands.get(i), i);
//
//                    // Test connection
//                    AcknowledgementPacket packet = new AcknowledgementPacket();
//
//                    out.write(packet.toString());
//                    out.flush();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            return null;
//        }
//    }

    class NewTurnTask extends AsyncTask<Player, Void, Void> {

        @Override
        protected Void doInBackground(Player... players) {
            for(Player p: players) {
                SocketWrapper s = p.getSocketWrapper();
                BufferedWriter out = s.getWriter();
            }
            return null;
        }
    }
}
