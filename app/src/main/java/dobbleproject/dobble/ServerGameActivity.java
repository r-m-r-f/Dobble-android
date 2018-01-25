package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
    Context mContext = this;

    // Threads
    ArrayList<ServerGameSocketReader> socketReaders = new ArrayList<>();
    ArrayList<ServerGameSocketWriter> socketWriters = new ArrayList<>();

    ArrayList<Handler> writerHandlers = new ArrayList<>();

    ArrayList<String> playersNames;

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
                    case MessageType.SELECTED_PICTURE: {
                        Bundle b = msg.getData();

                        int selectedPicture = b.getInt("picture");
                        int selectedCard = b.getInt("card");
                        int number = b.getInt("number");

                        Log.d("server", "matches: " + checkMatching(selectedPicture));

                        Handler writerHandler = socketWriters.get(number).getHandler();
                        Message message = new Message();

                        if (checkMatching(selectedPicture)) {
                            message.what = MessageType.CONFIRMED_SELECTION;
                            currentCardIndex = selectedCard;
                            displayCard();
                        } else {
                            message.what = MessageType.WRONG_SELECTION;
                        }
                        writerHandler.sendMessage(message);
                    }
                        break;
                    case MessageType.HAND_CLEARED: {
                        int winner = msg.getData().getInt("number");

                        Message message = new Message();
                        message.what = MessageType.END_GAME;

                        Bundle b = new Bundle();
                        b.putInt("winner", winner);

                        message.setData(b);

                        for (Handler wHandler: writerHandlers) {
                            wHandler.sendMessage(message);
                        }

                        String winnerName = playersNames.get(winner);
                        promptEndGame(winnerName);
                    }
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Socket threads setup
        for(int i=0; i < numberOfPlayers; i++) {
            ServerGameSocketReader reader = new ServerGameSocketReader(i, mHandler);
            socketReaders.add(reader);
            reader.start();

            ServerGameSocketWriter writer = new ServerGameSocketWriter(ServerPlayersList.getPlayer(i).getWriterSocket());
            socketWriters.add(writer);
            writer.start();

            writerHandlers.add(writer.getHandler());
        }

        playersNames = ServerPlayersList.getPlayersNames();
        for(int i = 0; i < numberOfPlayers; i++) {
            Handler wHandler = socketWriters.get(i).getHandler();

            Message message = new Message();
            message.what = MessageType.GAME_SETUP;

            Bundle b = new Bundle();
            b.putStringArrayList("players", playersNames);
            b.putInt("number", i);

            message.setData(b);
            wHandler.sendMessage(message);
        }

        // Create
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

        displayCard();



        // Send hands
        for(int i = 0; i < numberOfPlayers; i++) {
            Handler wHandler = socketWriters.get(i).getHandler();

            Message message = new Message();
            message.what = MessageType.NEW_HAND;

            Bundle b = new Bundle();
            b.putIntegerArrayList("hand", playersHands.get(i));

            message.setData(b);
            wHandler.sendMessage(message);
        }

        // Start a new game
        for(int i = 0; i < numberOfPlayers; i++) {
            Handler wHandler = socketWriters.get(i).getHandler();

            Message message = new Message();
            message.what = MessageType.NEW_GAME;

            wHandler.sendMessage(message);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    // HELPER METHODS

    // DO NOT EDIT!
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

    private void promptEndGame(String winnerName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder
                .setTitle("End of Game")
                .setMessage(winnerName + " won! Play again?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something
//                        Intent intent = new Intent(PlayerGameActivity.this, PlayerActivity.class);
//                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cleanup() {

    }
}
