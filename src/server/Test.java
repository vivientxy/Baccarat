package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

import client.Player;

public class Test {
    public static void main(String[] args) {
        Deck deck = new Deck(4);
        deck.shuffle();
        deck.printDeck();

        // Player a = new Player("Alice", new BigDecimal(50.1111));
        // System.out.println(a.getWallet());

        try {
            saveDeck(deck);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveDeck(Deck deck) throws IOException {
        // save cards to a cards.db file in current directory
        File cardDB = new File("." + File.separator + "cards.db");
        if (!cardDB.exists()) {
            cardDB.createNewFile();
        }
        OutputStream os = new FileOutputStream(cardDB);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);
        for (Float card : deck.getCards()) {
            bw.write(String.valueOf(card));
            bw.newLine();
        }
        writer.flush();
        bw.flush();
        os.flush();
        writer.close();
        bw.close();
        os.close();
    }
}
