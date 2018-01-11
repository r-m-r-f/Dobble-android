package dobbleproject.dobble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ServerSetupActivity extends AppCompatActivity {
    Button startButton;

    EditText serverNameBox;
    EditText numberOfPlayersBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setup);

        startButton = findViewById(R.id.startServerButton);
        serverNameBox = findViewById(R.id.serverNameEditText);
        numberOfPlayersBox = findViewById(R.id.numberOfPlayersEditText);

        startButton.setOnClickListener(new View.OnClickListener() {
            String serverName = null;
            Integer numberOfPlayers = null;

            @Override
            public void onClick(View view) {
                try {
                    serverName = serverNameBox.getText().toString();
                    numberOfPlayers = Integer.parseInt(numberOfPlayersBox.getText().toString());
                } catch (NumberFormatException e) {
                    // TODO: Handle invalid number of players
                }

                if(serverName != null && !serverName.isEmpty() && numberOfPlayers != null && numberOfPlayers > 0) {
                    Intent intent = new Intent(ServerSetupActivity.this, ServerActivity.class);
                    intent.putExtra("serverName", serverName);
                    intent.putExtra("numberOfPlayers", numberOfPlayers);

                    startActivity(intent);
                    finish();
                }


            }
        });
    }
}
