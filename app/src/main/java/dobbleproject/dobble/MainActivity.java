package dobbleproject.dobble;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Button clientButton, serverButton;
    TextView textView;

    Handler mHandler = new Handler();
    // Server or client thread
    GameJobThread thread;
    boolean isJobRunning = false;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set context
        mContext = this.getApplicationContext();

        // Get UI components

        textView = findViewById(R.id.textView);
        clientButton = findViewById(R.id.clientButton);
        serverButton = findViewById(R.id.serverButton);

        textView.setText("");

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


        clientButton.setText("Start Client");
        // Set clientButton onClickListener
        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isJobRunning) {
                    isJobRunning = true;
                    thread = new ClientThread(mContext, mHandler, "Client");
                    thread.start();
                    Log.d("main", "client started");
                    clientButton.setText("Stop Client");
                } else if(thread instanceof ClientThread) {
                    thread.endJob();
                    clientButton.setText("Start Client");
                    isJobRunning = false;
                }
            }
        });


        serverButton.setText("Start Server");
        // Set serverButton onClickListener
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isJobRunning) {
                    isJobRunning = true;
                    thread = new ServerThread(mContext, mHandler, "Server");
                    thread.start();
                    serverButton.setText("Stop Server");
                    textView.setText("");
                } else if(thread instanceof ServerThread) {
                    thread.endJob();
                    serverButton.setText("Start Server");
                    isJobRunning = false;
                }
            }
        });

    }
}
