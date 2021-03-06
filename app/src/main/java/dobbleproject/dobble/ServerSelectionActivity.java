package dobbleproject.dobble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dobbleproject.dobble.Player.PlayerServerDiscovery;
import dobbleproject.dobble.Player.PlayerWriterSocketHandler;
import dobbleproject.dobble.Player.PlayerRegisterRequest;
import dobbleproject.dobble.Server.ServerInfo;

public class ServerSelectionActivity extends AppCompatActivity {
    //TODO: Pass player playerName from other activity
    private Handler mHandler;

    private WifiManager wifiManager;
    private WifiManager.MulticastLock mLock;

    private String playerIp;
    private String playerName;
    private int playerSocketPort;

    // All possible threads using sockets
    private PlayerServerDiscovery playerServerDiscovery = null;
    private PlayerRegisterRequest registerRequest = null;

    // TODO: Refactor list of servers
    // <ip, name>
    HashMap<String, String> knownServers = new HashMap<>();
    ArrayList<ServerInfo> serversList = new ArrayList<>();
    ListView serverList = null;

    ArrayAdapter<ServerInfo> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selection);

        adapter = new ServerItemAdapter(this, R.layout.serverlist_item_layout, serversList);

        serverList = findViewById(R.id.serverListView);
        serverList.setAdapter(adapter);
        serverList.setClickable(true);

        serverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServerInfo item = adapter.getItem(i);

                if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
                    playerServerDiscovery.quit();
                }
                registerRequest = new PlayerRegisterRequest(playerName, playerIp, item.getIp(), item.getPort(), mHandler);
                registerRequest.start();
            }
        });

        // Set player name
        Bundle b = getIntent().getExtras();
        playerName = b.getString("playerName");

        try {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Fix server discovery
            mLock = wifiManager.createMulticastLock("lock");
            mLock.acquire();

            playerIp = WifiHelper.getIpAddress(wifiManager);

            // Set player tcp socket
            Socket playerSocket = new Socket();
            playerSocket.bind(null);
            PlayerWriterSocketHandler.setSocket(playerSocket);
            playerSocketPort = PlayerWriterSocketHandler.getPort();

            Log.d("player tcp port: ", Integer.toString(playerSocketPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler = new Handler(callback);
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
        stopThreads();
        if (mLock != null) {
            mLock.release();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private synchronized void stopThreads() {
        if(playerServerDiscovery != null && playerServerDiscovery.isAlive()) {
            playerServerDiscovery.quit();
        }

        if(registerRequest != null && registerRequest.isAlive()) {
            registerRequest.quit();
        }
    }

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MessageType.SERVER_DISCOVERED:
                    ServerInfo serverInfo = (ServerInfo) msg.getData().getParcelable("info");
                    Log.d("selection", "got announcement from " + serverInfo.getName() + " " + serverInfo.getIp());
                    if(knownServers.get(serverInfo.getIp()) == null) {
                        knownServers.put(serverInfo.getIp(), serverInfo.getName());
                        serversList.add(serverInfo);
                    }
                    adapter.notifyDataSetChanged();
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
                    finish();
                    //Start a new activity
                    Intent intent = new Intent(ServerSelectionActivity.this, PlayerGameActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };
}

class ServerItemAdapter extends ArrayAdapter<ServerInfo> {

    private List<ServerInfo> objects;

    public ServerItemAdapter(@NonNull Context context, int resource, @NonNull List<ServerInfo> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ServerInfo item = null;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.serverlist_item_layout, null);
        }

        if(objects != null) {
            item = objects.get(position);
        }

        if(item != null) {
            TextView tv = v.findViewById(R.id.serverName);
            tv.setText(item.getName());
        }

        return v;
    }
}
