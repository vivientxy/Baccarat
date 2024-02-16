package sg.edu.nus.iss.baccarat.server;

import java.net.Socket;

public class BaccaratEngine implements Runnable {
    private Socket socket;
    private int numOfDecks;
    private Deck bigDeck;

    public BaccaratEngine(Socket socket, int numOfDecks) {
        this.socket = socket;
        this.numOfDecks = numOfDecks;
        // instantiate the corresponding number of decks, shuffle all decks together
        this.bigDeck = new Deck(this.numOfDecks);
        this.bigDeck.shuffle();
    }

    @Override
    public void run() {
        





    }

}