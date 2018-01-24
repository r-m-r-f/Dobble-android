package dobbleproject.dobble.Game;

import java.util.ArrayList;
import java.util.Stack;

public abstract class Deck {
    // Number of pictures on a card
    protected int order;

    protected ArrayList<Card> cards = new ArrayList<>();


//    public Stack<Card> getCards() {
//        return cards;
//    }

//    Stack<Card> cards = new Stack<>();

    // TODO: throw IndexOutOfBoundsException
    public Card getCard(int index) {
        return cards.get(index);
    }

    public Stack<Integer> getCardsIndexes() {
        Stack<Integer> indexes = new Stack<>();

        for(int i=0; i < cards.size(); i++) {
            indexes.push(i);
        }
        return indexes;
    }

    protected abstract void createDeck();

}
