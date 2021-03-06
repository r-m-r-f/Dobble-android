package dobbleproject.dobble.Server;

import java.io.IOException;
import java.util.ArrayList;

import dobbleproject.dobble.SocketWrapper;

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
            p.closeSockets();
        }
        ServerPlayersList.players.clear();
    }

    public synchronized static int getSize() {
        return players.size();
    }

    public static synchronized ArrayList<Player> getList() {
        return players;
    }

    public static synchronized ArrayList<String> getPlayersNames() {
        ArrayList<String> names = new ArrayList<>();

        for(Player player: players) {
            names.add(player.getPlayerInfo().getName());
        }

        return names;
    }
}
