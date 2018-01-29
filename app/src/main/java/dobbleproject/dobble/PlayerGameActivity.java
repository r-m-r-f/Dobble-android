package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Game.Deck;
import dobbleproject.dobble.Game.Deck4;
import dobbleproject.dobble.Player.PlayerReaderSocketHandler;
import dobbleproject.dobble.Player.PlayerWriterSocketHandler;
import dobbleproject.dobble.Player.PlayerSocketReader;
import dobbleproject.dobble.Player.PlayerSocketWriter;

public class PlayerGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();
    int cardsLeft;

    protected TextView number;
    protected TextView playersNamesTextView;
    protected RelativeLayout cardBackground;

    // TODO: Create game setup
    protected int playerNumber = -1;

    ArrayList<Integer> handCardsIndexes;
    int currentHandIndex;

    ArrayList<String> playerNames;

    // TODO: Consider making deck static
    Deck deck = new Deck4();

    // blocks picture selection when a player exchanges information with the server
    boolean isClickable = false;

    private PlayerGameHandler mHandler;
    private WeakReference<Handler> writerHandler;
    private final Context mContext = this;

    // Threads
    PlayerSocketReader socketReader;
    PlayerSocketWriter socketWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        number = findViewById(R.id.cardsLeft);
        playersNamesTextView = findViewById(R.id.players_names_textview);
        // TODO: Move background colors to colors.xml
        cardBackground = findViewById(R.id.player_card_background);
        cardBackground.setBackgroundColor(Color.LTGRAY);

        setImagesFromResources();
        setCardImages();
        setListeners();

        mHandler = new PlayerGameHandler(this);

//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                Message message;
//                Bundle b;
//                switch (msg.what){
//                    case MessageType.GAME_SETUP:
//                        playerNames = msg.getData().getStringArrayList("names");
//                        playerNumber = msg.getData().getInt("playerNumber");
//
//                        StringBuilder sb = new StringBuilder();
//                        for(int i = 0; i < playerNames.size(); i++) {
//                            sb.append(playerNames.get(i));
//                            sb.append(" ");
//                        }
//
//                        playersNamesTextView.setText(sb.toString());
//                        break;
//                    case MessageType.HAND_DELIVERED:
//                        handCardsIndexes = msg.getData().getIntegerArrayList("hand");
//                        Toast.makeText(mContext, "got hand: size " + handCardsIndexes.size(), Toast.LENGTH_LONG).show();
//                        currentHandIndex = 0;
//
//                        message = new Message();
//                        message.what = MessageType.PLAYER_READY;
//
//                        b = new Bundle();
//                        b.putInt("number", playerNumber);
//
//                        Log.d("client player ready", Integer.toString(playerNumber));
//                        message.setData(b);
//                        if(writerHandler.get() != null) {
//                            writerHandler.get().sendMessage(message);
//                        }
//                        break;
//                    case MessageType.NEW_GAME:
//                        updateCardsLeft();
//                        displayCard();
//                        isClickable = true;
//                        Toast.makeText(mContext, "New game!", Toast.LENGTH_SHORT).show();
//                        break;
//                    case MessageType.CONFIRMED_SELECTION:
//                        currentHandIndex++;
//                        cardBackground.setBackgroundColor(Color.LTGRAY);
//
//                        // Check if any cards left
//                        if(currentHandIndex < handCardsIndexes.size()) {
//                            displayCard();
//                            updateCardsLeft();
//                            isClickable = true;
//                        } else {
//                            message = new Message();
//                            message.what = MessageType.HAND_CLEARED;
//                            if(writerHandler.get() != null) {
//                                writerHandler.get().sendMessage(message);
//                            }                        }
//                        break;
//                    case MessageType.WRONG_SELECTION:
//                        Toast.makeText(mContext, "wrong picture selected!", Toast.LENGTH_LONG).show();
//
//                        // Block ui
//                        isClickable = false;
//                        cardBackground.setBackgroundColor(Color.RED);
//                        new CountDownTimer(1000, 500){
//                            @Override
//                            public void onTick(long l) {}
//
//                            @Override
//                            public void onFinish() {
//                                isClickable = true;
//                                cardBackground.setBackgroundColor(Color.LTGRAY);
//                            }
//                        }.start();
//                        break;
//                    case MessageType.END_GAME: {
//                        int winner = msg.getData().getInt("winner");
//                        String winnerName = winner == playerNumber ? "You": playerNames.get(winner);
//                        promptEndGame(winnerName);
//                    }
//                }
//            }
//        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        socketReader = new PlayerSocketReader(mHandler);
        socketReader.start();

        socketWriter = new PlayerSocketWriter(PlayerWriterSocketHandler.getSocket(), playerNumber);
        socketWriter.start();

        writerHandler = new WeakReference<>(socketWriter.getHandler());

    }

    @Override
    protected void onDestroy() {
        try {
            cleanup();
        } catch (IOException e) {
            Log.d("player activity", "exception in onDestroy");
            e.printStackTrace();
        }
        super.onDestroy();
    }

// HELPER METHODS

    //DO NOT EDIT
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
        cardImageView.add((ImageView)findViewById(R.id.image));
        cardImageView.add((ImageView)findViewById(R.id.image1));
        cardImageView.add((ImageView)findViewById(R.id.image2));
        cardImageView.add((ImageView)findViewById(R.id.image3));
    }


    private void displayCard() {
        Card card = deck.getCard(handCardsIndexes.get(currentHandIndex));
        List<Integer> indexes = card.getIndexes();
        for(int i=0; i < cardImageView.size(); i++) {
            ImageView view = cardImageView.get(i);
            int imageId = images.get(indexes.get(i));

            view.setImageResource(imageId);
            view.setTag(imageId);
        }
    }

    private void updateCardsLeft() {
        cardsLeft = handCardsIndexes.size() - currentHandIndex;
        number.setText(String.valueOf(cardsLeft));
    }

    private void setListeners(){
        for(final ImageView imageView : cardImageView){
            imageView.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   if(handCardsIndexes != null && isClickable) {
                       if(currentHandIndex < handCardsIndexes.size()) {
                           // TODO: Send selected picture to the server

                           int imageId = (Integer) imageView.getTag();
                           int imageIndex = images.indexOf(imageId);
                           int cardIndex = handCardsIndexes.get(currentHandIndex);

                           Bundle b = new Bundle();
                           b.putInt("card", cardIndex);
                           b.putInt("picture", imageIndex);

                           Message msg = new Message();
                           msg.what = MessageType.SELECTED_PICTURE;
                           msg.setData(b);
                           if(writerHandler.get() != null) {
                               writerHandler.get().sendMessage(msg);
                           }
                           isClickable = false;
                           cardBackground.setBackgroundColor(Color.GREEN);
                       }
                   }
                }
            });
        }
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
                        //finish();
                        finish();
                        Intent intent = new Intent(PlayerGameActivity.this, PlayerActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void cleanup() throws IOException {
//        mHandler.getLooper().quit();
        // Stop threads
        if(socketReader != null) {
            socketReader.quit();
            Log.d("player cleanup", "reader socket thread closed");
        }

        if (socketWriter != null) {
            socketWriter.quit();
            Log.d("player cleanup", "writer socket thread closed");
        }

        // Close sockets
        PlayerReaderSocketHandler.close();
        Log.d("player cleanup", "reader socket closed");
        PlayerWriterSocketHandler.close();
        Log.d("player cleanup", "writer socket closed");
    }

    static class PlayerGameHandler extends Handler {
        private WeakReference<PlayerGameActivity> ref;

        public PlayerGameHandler(PlayerGameActivity ref) {
            this.ref = new WeakReference<>(ref);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final PlayerGameActivity a = ref.get();
            if (ref == null) {
                return;
            }
            Message message;
            Bundle b;
            switch (msg.what){
                case MessageType.GAME_SETUP:
                    a.playerNames = msg.getData().getStringArrayList("names");
                    a.playerNumber = msg.getData().getInt("playerNumber");

                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < a.playerNames.size(); i++) {
                        sb.append(a.playerNames.get(i));
                        sb.append(" ");
                    }

                    a.playersNamesTextView.setText(sb.toString());
                    break;
                case MessageType.HAND_DELIVERED:
                    a.handCardsIndexes = msg.getData().getIntegerArrayList("hand");
                    Toast.makeText(a.mContext, "got hand: size " + a.handCardsIndexes.size(), Toast.LENGTH_LONG).show();
                    a.currentHandIndex = 0;

                    message = new Message();
                    message.what = MessageType.PLAYER_READY;

                    b = new Bundle();
                    b.putInt("number", a.playerNumber);

                    Log.d("client player ready", Integer.toString(a.playerNumber));
                    message.setData(b);
                    if(a.writerHandler.get() != null) {
                        a.writerHandler.get().sendMessage(message);
                    }
                    break;
                case MessageType.NEW_GAME:
                    a.updateCardsLeft();
                    a.displayCard();
                    a.isClickable = true;
                    Toast.makeText(a.mContext, "New game!", Toast.LENGTH_SHORT).show();
                    break;
                case MessageType.CONFIRMED_SELECTION:
                    a.currentHandIndex++;
                    a.cardBackground.setBackgroundColor(Color.LTGRAY);

                    // Check if any cards left
                    if(a.currentHandIndex < a.handCardsIndexes.size()) {
                        a.displayCard();
                        a.updateCardsLeft();
                        a.isClickable = true;
                    } else {
                        message = new Message();
                        message.what = MessageType.HAND_CLEARED;
                        if(a.writerHandler.get() != null) {
                            a.writerHandler.get().sendMessage(message);
                        }                        }
                    break;
                case MessageType.WRONG_SELECTION:
                    Toast.makeText(a.mContext, "wrong picture selected!", Toast.LENGTH_LONG).show();

                    // Block ui
                    a.isClickable = false;
                    a.cardBackground.setBackgroundColor(Color.RED);
                    new CountDownTimer(1000, 500){
                        @Override
                        public void onTick(long l) {}

                        @Override
                        public void onFinish() {
                            a.isClickable = true;
                            a.cardBackground.setBackgroundColor(Color.LTGRAY);
                        }
                    }.start();
                    break;
                case MessageType.END_GAME: {
                    int winner = msg.getData().getInt("winner");
                    String winnerName = winner == a.playerNumber ? "You": a.playerNames.get(winner);
                    a.promptEndGame(winnerName);
                    return;
                }
            }

        }
    }


}
