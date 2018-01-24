package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Game.Deck;
import dobbleproject.dobble.Game.Deck4;
import dobbleproject.dobble.Packet.NewHandPacket;
import dobbleproject.dobble.Packet.StartGamePacket;
import dobbleproject.dobble.Server.ServerGameSocketListener;
import dobbleproject.dobble.Server.ServerPlayersList;

public class ServerGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();

    Deck deck;
    int currentCardIndex;

    int numberOfPlayers;
    Handler mHandler;

    // Threads
    SendHandThread sendHand;
    ArrayList<ServerGameSocketListener> socketListeners = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_game);

        setImagesFromResources();
        setCardImages();

        numberOfPlayers = getIntent().getExtras().getInt("numberOfPlayers");

        deck = new Deck4();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageType.SELECTED_PICTURE:
                        Bundle b = msg.getData();

                        int selectedPicture = b.getInt("picture");
                        if(checkMatching(selectedPicture)){
                            currentCardIndex = b.getInt("card");
                            displayCard();
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        Stack<Integer> cards = deck.getCardsIndexes();
        Collections.shuffle(cards);

        currentCardIndex = cards.pop();

        ArrayList<ArrayList<Integer>> playersHands = new ArrayList<>();


        int numberOfCardsInHand = cards.size() / numberOfPlayers;

        // Create hands
        for(int i = 0; i < numberOfPlayers; i++ ){
            ArrayList<Integer> hand = new ArrayList<>();
            for(int j = 0; j < numberOfCardsInHand; j++) {
                hand.add(cards.pop());
            }
            playersHands.add(hand);
        }

        try {
            // Send hands to players, probably will block ui
            // TODO: Handle interruptions
            sendHand = new SendHandThread(playersHands);
            sendHand.start();
            sendHand.join();

            for(int i=0; i < numberOfPlayers; i++) {
                ServerGameSocketListener listener = new ServerGameSocketListener(i, mHandler);
                socketListeners.add(listener);
            }

            for(ServerGameSocketListener l: socketListeners) {
                l.start();
            }

            displayCard();

            new StartGameThread().start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        cardImageView.add((ImageView)findViewById(R.id.image_server));
        cardImageView.add((ImageView)findViewById(R.id.image1_server));
        cardImageView.add((ImageView)findViewById(R.id.image2_server));
        cardImageView.add((ImageView)findViewById(R.id.image3_server));
    }

    private void displayCard() {
        List<Integer> currentCardIndexes = deck.getCard(currentCardIndex).getIndexes();
        for(int i = 0; i < cardImageView.size(); i++) {
            cardImageView.get(i).setImageResource(images.get(currentCardIndexes.get(i)));
        }
    }

    private synchronized boolean checkMatching(int pictureIndex) {
        Card currentCard = deck.getCard(currentCardIndex);

        if(currentCard.getIndexes().contains(pictureIndex)) {
            return true;
        } else {
            return false;
        }
    }

    // THREADS
    class SendHandThread extends Thread {
        ArrayList<ArrayList<Integer>> hands;

        public SendHandThread(ArrayList<ArrayList<Integer>> hands) {
            this.hands = hands;
        }

        @Override
        public void run() {
            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
                SocketWrapper s = ServerPlayersList.getPlayer(i).getReaderSocket();
                try {
                    BufferedWriter out = s.getWriter();

                    NewHandPacket packet = new NewHandPacket(hands.get(i), i);

                    out.write(packet.toString());
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: Change to AsyncTask
    class StartGameThread extends Thread {

        @Override
        public void run() {
            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
                SocketWrapper s = ServerPlayersList.getPlayer(i).getReaderSocket();
                try {
                    BufferedWriter out = s.getWriter();

                    StartGamePacket packet = new StartGamePacket();

                    out.write(packet.toString());
                    out.flush();

                } catch (IOException e) {
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
//                SocketWrapper s = ServerPlayersList.getPlayer(i).getReaderSocket();
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

//    class NewTurnTask extends AsyncTask<Player, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Player... players) {
//            for(Player p: players) {
//                SocketWrapper s = p.getReaderSocket();
//                BufferedWriter out = s.getWriter();
//            }
//            return null;
//        }
//    }
}
