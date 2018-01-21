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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Player.PlayerSocketReader;

public class ClientGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
//    List<List<Integer>> indices  = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();
    int cardsLeft;
    TextView number;

    ArrayList<Card> hand = null;
    Card currentPlayerCard = null;
    Card currentServerCard = null;
    // Index of the correct image
    int correctImageIndex;
    int currentPlayerCardIndex;

    Handler mHandler;
    Context mContext;

    // Threads
    PlayerSocketReader socketReader;

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
                        hand = msg.getData().getParcelableArrayList("hand");
                        Toast.makeText(mContext, "got hand: size " + hand.size(), Toast.LENGTH_LONG).show();
                        currentPlayerCardIndex = 0;
                        currentPlayerCard = hand.get(currentPlayerCardIndex);

                        updateCardsLeft();
                        displayCard();
                        break;
                    case MessageType.NEW_TURN:
                        currentServerCard = msg.getData().getParcelable("card");
                        List<Integer> serverIndexes = currentServerCard.getIndexes();
                        serverIndexes.retainAll(currentPlayerCard.getIndexes());

                        correctImageIndex = serverIndexes.get(0);

                        Toast.makeText(mContext, "new turn! " + images.get(correctImageIndex), Toast.LENGTH_SHORT).show();
                        break;

                    case MessageType.DEBUG:
                        Toast.makeText(mContext, "server responded!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        number = findViewById(R.id.cardsLeft);
        setImagesFromResources();
//        setIndices();
        setCardImages();
        setListeners();
        //pickCard();

        socketReader = new PlayerSocketReader(mHandler);
        socketReader.start();
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

//    private void setIndices(){
//        indices.add(Arrays.asList(0,1,2,9));
//        indices.add(Arrays.asList(9,3,4,5));
//        indices.add(Arrays.asList(8,9,6,7));
//        indices.add(Arrays.asList(0,10,3,6));
//        indices.add(Arrays.asList(1,10,4,7));
//        indices.add(Arrays.asList(8,2,10,5));
//        indices.add(Arrays.asList(0,8,11,4));
//        indices.add(Arrays.asList(1,11,5,6));
//        indices.add(Arrays.asList(11,2,3,7));
//        indices.add(Arrays.asList(0,12,5,7));
//        indices.add(Arrays.asList(8,1,3,12));
//        indices.add(Arrays.asList(12,2,4,6));
//        indices.add(Arrays.asList(9,10,11,12));
//    }

    private void setCardImages(){
        cardImageView.add((ImageView)findViewById(R.id.image));
        cardImageView.add((ImageView)findViewById(R.id.image1));
        cardImageView.add((ImageView)findViewById(R.id.image2));
        cardImageView.add((ImageView)findViewById(R.id.image3));
    }

//    private void pickCard(){
//        if(cardsLeft>0) {
//            Random generator = new Random();
//            int idx = generator.nextInt(cardsLeft);
//
//            int i = 0;
//            for (ImageView imageView : cardImageView) {
//                imageView.setImageResource(images.get(indices.get(idx).get(i)));
//                i++;
//            }
//
//            indices.remove(idx);
//            cardsLeft--;
//            number.setText(String.valueOf(cardsLeft));
//        }
//        else {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder
//                    .setTitle("End of Game")
//                    .setMessage("Play again?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            //Yes button clicked, do something
//                            Intent intent = new Intent(ClientGameActivity.this, ClientActivity.class);
//                            startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }
//    }

    private void displayCard() {
//        int i = 0;
//        for(int idx : hand.get(currentPlayerCardIndex).getIndexes()) {
//            cardImageView.get(i).setImageResource(images.get(idx));
//            i++;
//        }
        List<Integer> indexes = hand.get(currentPlayerCardIndex).getIndexes();
        for(int i=0; i < cardImageView.size(); i++) {

            cardImageView.get(i)
                    .setImageResource(
                            images.get(
                                    indexes.get(i)
                            )
                    );
        }
    }

    private void updateCardsLeft() {
        cardsLeft = hand.size() - currentPlayerCardIndex - 1;
        number.setText(String.valueOf(cardsLeft));
    }

    private void setListeners(){
        for(final ImageView imageView : cardImageView){
            imageView.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   if(hand != null) {
                       if(cardsLeft > 0) {
                           currentPlayerCardIndex++;
                           displayCard();
                           updateCardsLeft();
                       } else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                           builder
                                   .setTitle("End of Game")
                                   .setMessage("Play again?")
                                   .setIcon(android.R.drawable.ic_dialog_alert)
                                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           //Yes button clicked, do something
                                           Intent intent = new Intent(ClientGameActivity.this, ClientActivity.class);
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
