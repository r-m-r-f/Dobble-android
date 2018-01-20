package dobbleproject.dobble.Player;

import java.util.logging.Handler;

import dobbleproject.dobble.Server.Player;

public class PlayerGameThread extends Thread {
    Handler uiHandler = null;

    public PlayerGameThread(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }
}
