package dobbleproject.dobble.Server;

import java.net.Socket;

import dobbleproject.dobble.Player.PlayerInfo;

public class Player {
    private PlayerInfo playerInfo;
    private Socket socket;

    public Player(PlayerInfo playerInfo, Socket socket) {
        this.playerInfo = playerInfo;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
