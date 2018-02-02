package dobbleproject.dobble;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayerActivity extends AppCompatActivity {

    Button joinGame;
    EditText playerNameBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        joinGame = findViewById(R.id.joinButton);
        playerNameBox = findViewById(R.id.playerNameBox);

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = playerNameBox.getText().toString();

                if(!playerName.isEmpty()) {
                    finish();
                    Intent intent = new Intent(PlayerActivity.this, ServerSelectionActivity.class);
                    intent.putExtra("playerName", playerName);
                    startActivity(intent);
                }
            }
        });
    }
}