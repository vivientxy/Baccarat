package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Float> cards = new ArrayList<>();

    // instantiate a new deck with 52 cards
    public Deck() {
        int[] value = {1,2,3,4,5,6,7,8,9,10,11,12,13};
        int[] suits = {1,2,3,4};
        for (int suit : suits) {
            for (int val : value) {
                this.cards.add(Float.parseFloat(val + "." + suit));
            }
        }
    }

    // instantiate n number of decks (each with 52 cards) within one arraylist
    public Deck(int numOfDecks) {
        int[] value = {1,2,3,4,5,6,7,8,9,10,11,12,13};
        int[] suits = {1,2,3,4};
        for (int i = 0; i < numOfDecks; i++) {
            for (int suit : suits) {
                for (int val : value) {
                    this.cards.add(Float.parseFloat(val + "." + suit));
                }
            }
        }
    }

    public List<Float> getCards() {
        return cards;
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Float drawCard() {
        Float card = this.cards.get(0);
        this.cards.remove(0);
        return card;
    }

    public void printDeck() {
        for (Float card : this.cards) {
            System.out.println(card);
        }
    }

}