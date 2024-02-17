package server;

import java.math.BigDecimal;

import client.Player;

public class Test {
    public static void main(String[] args) {
        Deck deck = new Deck(4);
        deck.shuffle();
        deck.printDeck();

        Player a = new Player("Alice", new BigDecimal(50.1111));
        System.out.println(a.getWallet());

    }
}
