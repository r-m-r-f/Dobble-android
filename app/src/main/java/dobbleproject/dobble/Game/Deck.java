package dobbleproject.dobble.Game;

import java.util.ArrayList;
import java.util.Stack;

public abstract class Deck {
    // Number of pictures on a card
    protected int order;

//    public ArrayList<Card> getCards() {
//        return cards;
//    }
//    ArrayList<Card> cards = new ArrayList<>();

    public Stack<Card> getCards() {
        return cards;
    }

    Stack<Card> cards = new Stack<>();

    protected abstract void createDeck();

}
