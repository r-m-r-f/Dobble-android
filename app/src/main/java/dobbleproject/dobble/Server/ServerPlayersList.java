package dobbleproject.dobble.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerPlayersList {
    private static ArrayList<Player> players = new ArrayList<>();

    public static synchronized void addPlayer(Player player) {
        ServerPlayersList.players.add(player);
    }

    public static synchronized Player getPlayer(int index) {
        // TODO: implement bound checking
        return ServerPlayersList.players.get(index);
    }

    public static synchronized void clearPlayers() throws IOException {
        // Close all sockets
        for(Player p : ServerPlayersList.players) {
            Socket socket = p.getSocket();
            if(socket != null) {
                socket.close();
            }
        }
        ServerPlayersList.players.clear();
    }

    public synchronized static int getSize() {
        return players.size();
    }

    public static synchronized ArrayList<Player> getList() {
        return players;
    }
}
