package dobbleproject.dobble.Server;

import dobbleproject.dobble.Player.PlayerInfo;
import dobbleproject.dobble.SocketWrapper;

public class Player {
    private PlayerInfo playerInfo;
    private SocketWrapper readerSocket;
    private SocketWrapper writerSocket;

    public Player(PlayerInfo playerInfo, SocketWrapper readerSocket, SocketWrapper writerSocket) {
        this.playerInfo = playerInfo;
        this.readerSocket = readerSocket;
        this.writerSocket = writerSocket;
    }

    public SocketWrapper getReaderSocket() {
        return readerSocket;
    }

    public SocketWrapper getWriterSocket() {
        return writerSocket;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
