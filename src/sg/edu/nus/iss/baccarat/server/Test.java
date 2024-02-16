package sg.edu.nus.iss.baccarat.server;

import java.math.BigDecimal;

import sg.edu.nus.iss.baccarat.client.Player;

public class Test {
    public static void main(String[] args) {
        Deck deck = new Deck(4);
        deck.shuffle();
        deck.printDeck();

        // Player a = new Player("Alice", new BigDecimal(50.1111));
        // System.out.println(a.getWallet());

    }
}
