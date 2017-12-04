package dobbleproject.dobble;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ClientActivity extends AppCompatActivity {

    Button joinGame;
    Handler mHandler = new Handler();
    // Server or client thread
    GameJobThread thread;
    boolean isJobRunning = false;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        joinGame = findViewById(R.id.joinButton);
        // Set context
        mContext = this.getApplicationContext();

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isJobRunning) {
                    isJobRunning = true;
                    thread = new ClientThread(mContext, mHandler, "Client");
                    thread.start();
                    Log.d("main", "client started");
                } else if(thread instanceof ClientThread) {
                    thread.endJob();
                    isJobRunning = false;
                }
            }
        });
    }
}
