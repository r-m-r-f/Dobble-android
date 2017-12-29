package dobbleproject.dobble;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    List<Integer> images = new ArrayList<>();
    List<List<Integer>> indices  = new ArrayList<>();
    List<ImageView> cardImages  = new ArrayList<>();
    int cardsLeft = 13;
    TextView number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        number = findViewById(R.id.cardsLeft);
        setImagesFromResources();
        setIndices();
        setCardImages();
        setListeners();
        pickCard();
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

    private void setIndices(){
        indices.add(Arrays.asList(0,1,2,9));
        indices.add(Arrays.asList(9,3,4,5));
        indices.add(Arrays.asList(8,9,6,7));
        indices.add(Arrays.asList(0,10,3,6));
        indices.add(Arrays.asList(1,10,4,7));
        indices.add(Arrays.asList(8,2,10,5));
        indices.add(Arrays.asList(0,8,11,4));
        indices.add(Arrays.asList(1,11,5,6));
        indices.add(Arrays.asList(11,2,3,7));
        indices.add(Arrays.asList(0,12,5,7));
        indices.add(Arrays.asList(8,1,3,12));
        indices.add(Arrays.asList(12,2,4,6));
        indices.add(Arrays.asList(9,10,11,12));
    }

    private void setCardImages(){
        cardImages.add((ImageView)findViewById(R.id.image));
        cardImages.add((ImageView)findViewById(R.id.image1));
        cardImages.add((ImageView)findViewById(R.id.image2));
        cardImages.add((ImageView)findViewById(R.id.image3));
    }

    private void pickCard(){
        if(cardsLeft>0) {
            Random generator = new Random();
            int idx = generator.nextInt(cardsLeft);

            int i = 0;
            for (ImageView imageView : cardImages) {
                imageView.setImageResource(images.get(indices.get(idx).get(i)));
                i++;
            }

            indices.remove(idx);
            cardsLeft--;
            number.setText(String.valueOf(cardsLeft));
        }
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("End of Game")
                    .setMessage("Play again?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Yes button clicked, do something
                            Intent intent = new Intent(GameActivity.this, ClientActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void setListeners(){
        for(ImageView imageView : cardImages){
            imageView.setOnClickListener( new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    pickCard();
                }
            });
        }
    }
}
