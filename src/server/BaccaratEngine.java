package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class BaccaratEngine implements Runnable {
    private Socket socket;
    private Deck bigDeck;

    public BaccaratEngine(Socket socket, int numOfDecks) {
        this.socket = socket;
        // instantiate the corresponding number of decks, shuffle all decks together
        this.bigDeck = new Deck(numOfDecks);
        this.bigDeck.shuffle();
    }

    @Override
    public void run() {
        System.out.println("Starting a client thread");
        NetworkIO netIO = null;
        boolean gameOn = true;
        while (gameOn) {
            try {
                netIO = new NetworkIO(this.socket);
                String clientRequest = "";
                // game loop
                // keep listening for client commands
                while (!(clientRequest = netIO.read()).toLowerCase().equals("exit")) {
                    decode(clientRequest);
                    
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // methods for the game
    public void saveDeck(Deck deck) throws IOException {
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

    public void decode(String clientRequest) {
        String[] command = clientRequest.toLowerCase().split(" ");
        switch (command[0]) {
            case "login":
                login(command[1], Integer.parseInt(command[2]));
                break;
            case "bet":
                bet(Integer.parseInt(command[1]));
                break;
            case "deal":
                deal(command[1]);
                break;
            default:
                break;
        }
    }

    public void login(String user, int amount) {
        // create a file named "kenneth.db" with the
        // value "100" as the content of the file.
    }

    public void bet(int amount) {
        // allow client to place a bet of 50 on the current session
    }

    public void deal(String side) {
        // assign the bet (deal B or P)
        // deal the cards - 2 to player, 2 to broker
        // calc value if need to draw 3rd card
        
    }

}