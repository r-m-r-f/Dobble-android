package dobbleproject.dobble.Server;

import java.util.ArrayList;

public class GameState {
    private ArrayList<String> names;
    private ArrayList<Integer> points;

    public GameState(ArrayList<String> names) {
        this.names = names;
        points = new ArrayList<>(names.size());
    }

    public synchronized void addPoint(int playerNumber) {
        int playerPoints = points.get(playerNumber);
        points.set(playerNumber, playerPoints++);
    }
}
