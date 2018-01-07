package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import dobbleproject.dobble.Player.PlayerServerDiscovery;
import dobbleproject.dobble.Player.PlayerSocketHandler;
import dobbleproject.dobble.Player.PlayerRegisterRequest;
import dobbleproject.dobble.Server.ServerInfo;

public class ServerSelectionActivity extends AppCompatActivity {
    //TODO: Pass player playerName from other activity
    private Handler mHandler;

    private WifiManager wifiManager;

    private String playerIp;
    private String playerName;
    private int playerSocketPort;

    // All possible threads using sockets
    private PlayerServerDiscovery playerServerDiscovery = null;
    private PlayerRegisterRequest registerRequest = null;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selection);

        // Set player name
        Bundle b = getIntent().getExtras();
        playerName = b.getString("playerName");

        try {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            playerIp = WifiHelper.getIpAddress(wifiManager);

            // Set player tcp socket
            Socket playerSocket = new Socket();
            playerSocket.bind(null);
            PlayerSocketHandler.setSocket(playerSocket);
            playerSocketPort = PlayerSocketHandler.getPort();

            Log.d("player tcp port: ", Integer.toString(playerSocketPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageType.SERVER_DISCOVERED:
                        ServerInfo serverInfo = (ServerInfo) msg.getData().getParcelable("info");
                        Log.d("selection", "got announcement from " + serverInfo.getName() + " " + serverInfo.getIp());

                        // Stop server discovery to free listener port
                        // TODO: Move to button listener
                        if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
                            playerServerDiscovery.quit();
                        }
                        registerRequest = new PlayerRegisterRequest(playerName, playerIp, serverInfo.getIp(), serverInfo.getPort(), mHandler);
                        registerRequest.start();
                        break;
                    case MessageType.REGISTER_REQUEST_EXPIRED:
                        Toast.makeText(getApplicationContext(), "Server not responding!", Toast.LENGTH_LONG);

                        // Restart server discovery, probably unsafe
                        if(playerServerDiscovery != null) {
                            playerServerDiscovery.quit();
                            playerServerDiscovery = new PlayerServerDiscovery(mHandler);
                        }
                        break;
                    case MessageType.REGISTER_REQUEST_ERROR:
                        Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_LONG);

                        // Restart server discovery, probably unsafe
                        if(playerServerDiscovery != null) {
                            playerServerDiscovery.quit();
                            playerServerDiscovery = new PlayerServerDiscovery(mHandler);
                        }
                        break;
                    case MessageType.PLAYER_REGISTERED:
                        Log.d("player registered", ServerSelectionActivity.this.toString());
                        //Start a new activity
                        Intent intent = new Intent(ServerSelectionActivity.this, GameActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        playerServerDiscovery = new PlayerServerDiscovery(mHandler);
        playerServerDiscovery.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
            playerServerDiscovery.quit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
            playerServerDiscovery.quit();
        }
    }

    private synchronized void stopThreads() {
        if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
            playerServerDiscovery.quit();
        }

        if(registerRequest != null && registerRequest.isAlive()) {
            registerRequest.quit();
        }
    }
}