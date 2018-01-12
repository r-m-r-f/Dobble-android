package dobbleproject.dobble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChoosingModeActivity extends AppCompatActivity {

    Button masterButton, playerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_mode);

        masterButton = findViewById(R.id.masterButton);
        playerButton = findViewById(R.id.playerButton);

        masterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoosingModeActivity.this, ServerSetupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoosingModeActivity.this, ClientActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
