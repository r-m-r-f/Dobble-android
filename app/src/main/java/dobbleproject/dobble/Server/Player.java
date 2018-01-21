package dobbleproject.dobble.Server;

import java.net.Socket;

import dobbleproject.dobble.Player.PlayerInfo;
import dobbleproject.dobble.SocketWrapper;

public class Player {
    private PlayerInfo playerInfo;
    //private Socket socket;
    private SocketWrapper socketWrapper;

    public Player(PlayerInfo playerInfo, SocketWrapper socket) {
        this.playerInfo = playerInfo;
        this.socketWrapper = socket;
    }

    public SocketWrapper getSocketWrapper() {
        return socketWrapper;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
