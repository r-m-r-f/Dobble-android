package dobbleproject.dobble;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ServerActivity extends AppCompatActivity {

    Button startButton;
    TextView textView;
    Handler mHandler = new Handler();
    // Server or client thread
    GameJobThread thread;
    boolean isJobRunning = false;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        startButton = findViewById(R.id.startButton);
        textView = findViewById(R.id.textView4);

        // Set context
        mContext = this.getApplicationContext();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ServerMessage.ERROR:
                        String message = (String) msg.getData().get("msg");
                        textView.append(msg.getData().getString("msg") + "\n");
                        break;
                    case ServerMessage.TEST:
                        textView.append(msg.getData().getString("msg") + "\n");
                        break;
                }
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isJobRunning) {
                    isJobRunning = true;
                    thread = new ServerThread(mContext, mHandler, "Server");
                    thread.start();
                    textView.setText("");
                } else if(thread instanceof ServerThread) {
                    thread.endJob();
                    isJobRunning = false;
                }
            }
        });

    }
}
