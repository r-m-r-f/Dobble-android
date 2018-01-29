package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;

import dobbleproject.dobble.Player.PlayerInfo;
import dobbleproject.dobble.Server.Player;
import dobbleproject.dobble.Server.ServerPlayerRegistration;
import dobbleproject.dobble.Server.ServerAnnouncement;
import dobbleproject.dobble.Server.ServerPlayersList;
import dobbleproject.dobble.Server.ServerSocketSingleton;

public class ServerActivity extends AppCompatActivity {
    Button startButton;
    TextView textView;
    Handler mHandler;

    boolean isJobRunning = false;

    WifiManager wifiManager;

    // TODO: Add custom server name
    String serverName;

    // Number of players
    int numberOfPlayers;

    private final Context mContext = this;

    String broadcastAddress;
    String serverIp;

    // Server threads
    ServerAnnouncement serverAnnouncement = null;
    ServerPlayerRegistration serverPlayerRegistration = null;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        // Set server name and player name
        Bundle b = getIntent().getExtras();
        serverName = b.getString("serverName");
        numberOfPlayers = b.getInt("numberOfPlayers");

        startButton = findViewById(R.id.startButton);
        textView = findViewById(R.id.textView4);

        // Set context
//        mContext = this.getApplicationContext();

        try {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            serverIp = WifiHelper.getIpAddress(wifiManager);
            broadcastAddress = WifiHelper.getBroadcastAddress(wifiManager);

        } catch (Exception e) {
            e.printStackTrace();
        }



        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageType.DEBUG:
                        textView.append(msg.getData().getString("msg") + "\n");
                        break;
                    case MessageType.PLAYERS_LIST_READY:
                        if(serverAnnouncement != null && serverAnnouncement.isAlive()) {
                            serverAnnouncement.quit();
                        }
                        for(Player p : ServerPlayersList.getList()) {
                            PlayerInfo pf = p.getPlayerInfo();
                            textView.append("List: registred " + pf.getName() + " " + pf.getIp() + "\n");
                        }
                        isJobRunning = false;

                        finish();

                        Intent i = new Intent(ServerActivity.this, ServerGameActivity.class);
                        i.putExtra("numberOfPlayers", numberOfPlayers);
                        startActivity(i);
                        break;
                    // TODO: Handle other message types
                }
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isJobRunning) {
                    isJobRunning = true;

                    //Set server socket
                    try {
                        ServerSocket ss = ServerSocketSingleton.getServerSocket();
                        if(ss != null) {
                            ss.close();
                        }
                        ss = new ServerSocket(0, numberOfPlayers);
                        ServerSocketSingleton.setServerSocket(ss);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }

                    serverAnnouncement = new ServerAnnouncement(serverName, serverIp, broadcastAddress, mHandler);
                    serverPlayerRegistration = new ServerPlayerRegistration(serverName, serverIp, numberOfPlayers, mHandler);

                    textView.setText("");

                    serverAnnouncement.start();
                    serverPlayerRegistration.start();
                } else {
                    stopThreads();
                    isJobRunning = false;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopThreads();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThreads();
    }

    private void stopThreads() {
        if(serverAnnouncement != null && serverAnnouncement.isAlive()) {
            serverAnnouncement.quit();
        }

        if(serverPlayerRegistration != null && serverPlayerRegistration.isAlive()) {
            serverPlayerRegistration.quit();
        }
    }
}
