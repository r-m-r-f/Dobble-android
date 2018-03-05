package dobbleproject.dobble.Game;


import java.util.ArrayList;
import java.util.Arrays;

public class Deck4 extends Deck {

    public Deck4() {
//        this.order = 4;

        createDeck();

    }

    @Override
    protected void createDeck() {
        cards.add(new Card(Arrays.asList(0,1,2,9)));
        cards.add(new Card(Arrays.asList(9,3,4,5)));
        cards.add(new Card(Arrays.asList(8,9,6,7)));
        cards.add(new Card(Arrays.asList(0,10,3,6)));
        cards.add(new Card(Arrays.asList(1,10,4,7)));
        cards.add(new Card(Arrays.asList(8,2,10,5)));
        cards.add(new Card(Arrays.asList(0,8,11,4)));
        cards.add(new Card(Arrays.asList(1,11,5,6)));
        cards.add(new Card(Arrays.asList(11,2,3,7)));
        cards.add(new Card(Arrays.asList(0,12,5,7)));
        cards.add(new Card(Arrays.asList(8,1,3,12)));
        cards.add(new Card(Arrays.asList(12,2,4,6)));
        cards.add(new Card(Arrays.asList(9,10,11,12)));
    }

//    public Card getCard(int index) {
//        return cards.get(index);
//    }
}
