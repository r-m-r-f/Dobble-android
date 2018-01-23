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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dobbleproject.dobble.Game.Card;
import dobbleproject.dobble.Packet.NewTurnPacket;
import dobbleproject.dobble.Packet.RegisterRequestPacket;
import dobbleproject.dobble.Player.PlayerSocketHandler;
import dobbleproject.dobble.Player.PlayerSocketReader;
import dobbleproject.dobble.Server.ServerPlayersList;

public class ClientGameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    //    List<List<Integer>> indices  = new ArrayList<>();
    List<ImageView> cardImageView = new ArrayList<>();
    HashMap<Integer, Integer> cardImages = new HashMap<>();
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
                switch (msg.what) {
                    case MessageType.HAND_DELIVERED:
                        hand = msg.getData().getParcelableArrayList("hand");
                        Toast.makeText(mContext, "got hand: size " + hand.size(), Toast.LENGTH_LONG).show();
                        currentPlayerCardIndex = 0;
                        currentPlayerCard = hand.get(currentPlayerCardIndex);

                        updateCardsLeft();
                        displayCard();
                        setCardImages(currentPlayerCard.getIndexes());
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
        setCardImages();
        setListeners();

        socketReader = new PlayerSocketReader(mHandler);
        socketReader.start();
    }

    private void setImagesFromResources() {
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

    private void setCardImages() {
        cardImageView.add((ImageView) findViewById(R.id.image));
        cardImageView.add((ImageView) findViewById(R.id.image1));
        cardImageView.add((ImageView) findViewById(R.id.image2));
        cardImageView.add((ImageView) findViewById(R.id.image3));
    }

    private void setCardImages(List<Integer> indexes) {
        cardImages.put(R.id.image, indexes.get(0));
        cardImages.put(R.id.image1, indexes.get(1));
        cardImages.put(R.id.image2, indexes.get(2));
        cardImages.put(R.id.image3, indexes.get(3));
    }


    private void displayCard() {
//        int i = 0;
//        for(int idx : hand.get(currentPlayerCardIndex).getIndexes()) {
//            cardImageView.get(i).setImageResource(images.get(idx));
//            i++;
//        }
        List<Integer> indexes = hand.get(currentPlayerCardIndex).getIndexes();
        for (int i = 0; i < cardImageView.size(); i++) {

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

    private void setListeners() {
        for (final ImageView imageView : cardImageView) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hand != null) {
                        if (cardsLeft > 0) {
                            if (correctImageIndex == cardImages.get(v.getId())) {
                                Toast.makeText(mContext, "Git", Toast.LENGTH_SHORT).show();
                                SendCardThread sendCardThread = new SendCardThread(hand.get(currentPlayerCardIndex));
                                sendCardThread.start();
                            }
                            else {
                                Toast.makeText(mContext, "Dupa", Toast.LENGTH_SHORT).show();
                            }
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

class SendCardThread extends Thread {
    Card card;
    private SocketWrapper playerSocket = null;

    public SendCardThread(Card card) {
        this.card = card;
    }

    @Override
    public void run() {
        try {
            playerSocket = PlayerSocketHandler.getSocket();
            // Sleep before writing
            sleep(300);

            BufferedWriter out = playerSocket.getWriter();

            out.write(new NewTurnPacket(card).getPayload().toString());
            Log.d("Client", "Send new Turn Packet");
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
