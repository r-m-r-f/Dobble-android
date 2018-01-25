package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import dobbleproject.dobble.Server.ServerGameSocketReader;
import dobbleproject.dobble.Server.ServerGameSocketWriter;
import dobbleproject.dobble.Server.ServerPlayersList;

public class ServerGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();

    Deck deck;
    int currentCardIndex;

    int numberOfPlayers;
    Handler mHandler;

    // Threads
//    SendHandThread sendHand;
    ArrayList<ServerGameSocketReader> socketReaders = new ArrayList<>();
    ArrayList<ServerGameSocketWriter> socketWriters = new ArrayList<>();

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
                        int selectedCard = b.getInt("card");
                        int number = b.getInt("number");

                        Log.d("server", "matches: " + checkMatching(selectedPicture));

                        Handler writerHandler = socketWriters.get(number).getHandler();
                        Message message = new Message();

                        if(checkMatching(selectedPicture)) {
                            message.what = MessageType.CONFIRMED_SELECTION;
                            currentCardIndex = selectedCard;
                            displayCard();
                        } else {
                            message.what = MessageType.WRONG_SELECTION;
                        }
                        writerHandler.sendMessage(message);

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
//            sendHand = new SendHandThread(playersHands);
//            sendHand.start();
//            sendHand.join();

            for(int i=0; i < numberOfPlayers; i++) {
                ServerGameSocketReader reader = new ServerGameSocketReader(i, mHandler);
                socketReaders.add(reader);
                reader.start();

                ServerGameSocketWriter writer = new ServerGameSocketWriter(ServerPlayersList.getPlayer(i).getWriterSocket());
                socketWriters.add(writer);
                writer.start();
            }

            displayCard();

            // Send hands
            for(int i = 0; i < numberOfPlayers; i++) {
                Handler wHandler = socketWriters.get(i).getHandler();

                Message message = new Message();
                message.what = MessageType.NEW_HAND;

                Bundle b = new Bundle();
                b.putIntegerArrayList("hand", playersHands.get(i));
                b.putInt("number", i);

                message.setData(b);
                wHandler.sendMessage(message);
            }

            // Start a new game
            for(int i = 0; i < numberOfPlayers; i++) {
                Handler wHandler = socketWriters.get(i).getHandler();

                Message message = new Message();
                message.what = MessageType.NEW_GAME;

//                Bundle b = new Bundle();
//                b.putIntegerArrayList("hand", playersHands.get(i));
//                b.putInt("number", i);
//
//                message.setData(b);
                wHandler.sendMessage(message);
            }

            //new StartGameThread().start();

        } catch (Exception e) { e.printStackTrace();}
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
        Log.d("server", currentCard.getIndexes().toString()+ " " + Integer.toString(pictureIndex));
        for(int i: currentCard.getIndexes()) {
            if(i == pictureIndex)
                return true;
        }
        return false;

    }

    // THREADS
//    class SendHandThread extends Thread {
//        ArrayList<ArrayList<Integer>> hands;
//
//        public SendHandThread(ArrayList<ArrayList<Integer>> hands) {
//            this.hands = hands;
//        }
//
//        @Override
//        public void run() {
//            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
//                SocketWrapper s = ServerPlayersList.getPlayer(i).getReaderSocket();
//                try {
//                    BufferedWriter out = s.getWriter();
//
//                    NewHandPacket packet = new NewHandPacket(hands.get(i), i);
//
//                    out.write(packet.toString());
//                    out.flush();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // TODO: Change to AsyncTask
//    class StartGameThread extends Thread {
//
//        @Override
//        public void run() {
//            for(int i = 0; i < ServerPlayersList.getSize(); i++) {
//                SocketWrapper s = ServerPlayersList.getPlayer(i).getReaderSocket();
//                try {
//                    BufferedWriter out = s.getWriter();
//
//                    StartGamePacket packet = new StartGamePacket();
//
//                    out.write(packet.toString());
//                    out.flush();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
}
