package sg.edu.nus.iss.baccarat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();

    // instantiate a new deck with 52 cards
    public Deck() {
        int[] value = {1,2,3,4,5,6,7,8,9,10,11,12,13};
        int[] suits = {1,2,3,4};
        for (int suit : suits) {
            for (int val : value) {
                cards.add(new Card(val, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card drawCard() {
        Card card = this.cards.getLast();
        this.cards.removeLast();
        return card;
    }

    public void printDeck() {
        for (Card card : this.cards) {
            System.out.println(card);
        }
    }

}