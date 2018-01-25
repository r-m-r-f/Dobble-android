package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Game.Deck;
import dobbleproject.dobble.Game.Deck4;
import dobbleproject.dobble.Player.PlayerWriterSocketHandler;
import dobbleproject.dobble.Player.PlayerSocketReader;
import dobbleproject.dobble.Player.PlayerSocketWriter;

public class PlayerGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();
    int cardsLeft;
    TextView number;

    // TODO: Create game setup
    int playerNumber = -1;

    ArrayList<Integer> handCardsIndexes;
    int currentHandIndex;

    // TODO: Consider making deck static
    Deck deck = new Deck4();

    // blocks picture selection when a player exchanges information with the server
    boolean isClickable = false;

    Handler mHandler;
    Context mContext;

    Handler writerHandler;

    // Threads
    PlayerSocketReader socketReader;
    PlayerSocketWriter socketWriter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mContext = this;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.HAND_DELIVERED:
                        handCardsIndexes = msg.getData().getIntegerArrayList("hand");
                        Toast.makeText(mContext, "got hand: size " + handCardsIndexes.size(), Toast.LENGTH_LONG).show();
                        currentHandIndex = 0;

                        updateCardsLeft();
                        displayCard();
                        break;
                    case MessageType.NEW_GAME:
                        isClickable = true;
                        Toast.makeText(mContext, "New game!", Toast.LENGTH_SHORT).show();
                        break;
                    case MessageType.CONFIRMED_SELECTION:
                        isClickable = true;
                        currentHandIndex++;
                        displayCard();
                        updateCardsLeft();
                        break;
                    case MessageType.WRONG_SELECTION:
                        Toast.makeText(mContext, "wrong picture selected!", Toast.LENGTH_LONG).show();

                        // Block ui
                        isClickable = false;
                        new CountDownTimer(2000, 500){
                            @Override
                            public void onTick(long l) {}

                            @Override
                            public void onFinish() {
                                isClickable = true;
                            }
                        };


                }
            }
        };

        number = findViewById(R.id.cardsLeft);
        setImagesFromResources();
        setCardImages();
        setListeners();

        socketReader = new PlayerSocketReader(mHandler);
        socketReader.start();

        socketWriter = new PlayerSocketWriter(PlayerWriterSocketHandler.getSocket(), playerNumber);
        socketWriter.start();

        writerHandler = socketWriter.getHandler();
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
        cardsLeft = handCardsIndexes.size() - currentHandIndex - 1;
        number.setText(String.valueOf(cardsLeft));
    }

    private void setListeners(){
        for(final ImageView imageView : cardImageView){
            imageView.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   if(handCardsIndexes != null && isClickable) {
                       if(cardsLeft > 0) {
                           // TODO: Send selected picture to the server

                           int imageId = (Integer) imageView.getTag();
                           Log.d("img tag", Integer.toString(imageId));
                           int imageIndex = images.indexOf(imageId);
                           Log.d("idex of: ", Integer.toString(imageIndex));
                           int cardIndex = handCardsIndexes.get(currentHandIndex);

                           Log.d("player", "card" + deck.getCard(cardIndex).getIndexes() + ", picture "+ Integer.toString(imageIndex));

                           Bundle b = new Bundle();
                           b.putInt("card", cardIndex);
                           b.putInt("picture", imageIndex);

                           Message msg = new Message();
                           msg.what = MessageType.SELECTED_PICTURE;
                           msg.setData(b);
                           writerHandler.sendMessage(msg);

                           isClickable = false;

//                           currentHandIndex++;
//                           displayCard();
//                           updateCardsLeft();
                       } else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                           builder
                                   .setTitle("End of Game")
                                   .setMessage("Play again?")
                                   .setIcon(android.R.drawable.ic_dialog_alert)
                                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           //Yes button clicked, do something
                                           Intent intent = new Intent(PlayerGameActivity.this, PlayerActivity.class);
                                           startActivity(intent);
                                       }
                                   })
                                   .setNegativeButton("No", null)
                                   .show();
                       }

                   }
                }
            });
        }
    }
}
