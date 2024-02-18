package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
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
                    String[] command = clientRequest.toLowerCase().split(" ");
                    String message = "";
                    // client command will come in as "username " + command
                    switch (command[1]) {
                        case "login":
                            message = login(command[2], Integer.parseInt(command[3]));
                            break;
                        case "bet":
                            message = bet(command[0], Integer.parseInt(command[2]));
                            break;
                        case "deal":
                            message = deal();
                            break;
                        default:
                            break;
                    }
                    netIO.write(message);
                }
                if (clientRequest.equals("exit")) {
                    gameOn = false;
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

    public String login(String user, int amount) throws IOException {
        String message = "";
        // create a file named "kenneth.db" with the
        // value "100" as the content of the file.
        File folder = new File("playersDB");
        File playerDB = new File(folder + File.separator + user + ".db");
        if (!playerDB.exists()) {
            playerDB.createNewFile();
        }
        OutputStream os = new FileOutputStream(playerDB);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(amount);
        writer.flush();
        os.flush();
        writer.close();
        os.close();
        message = "Login successful. $" + amount + " added to " + user + "'s account.";
        return message;
    }

    public String bet(String command, int amount) throws IOException {
        String message = "";
        // check against playersDB if client has sufficient funds
        File folder = new File("playersDB");        
        if (folder.exists()) {
            for (File playerDB : folder.listFiles()) {
                if (playerDB.getName().equals(command)) {
                    int playerWallet;
                    InputStream is = new FileInputStream(playerDB);
                    InputStreamReader reader = new InputStreamReader(is);
                    playerWallet = reader.read();
                    reader.close();
                    is.close();
                    // check if wallet is sufficient
                    if (playerWallet >= amount) {
                        message = "Bet accepted";
                    } else {
                        message = "Bet rejected - Player unable to afford bet";
                    }
                } else {
                    message = "Bet rejected - Player does not exist!";
                }
            }
        } else {
            // playerDB folder does not exist
            message = "Bet rejected - Player does not exist!";
        }
        return message;
    }

    public String deal() {
        // https://www.gra.gov.sg/docs/default-source/game-rules/rws/semi-automated-etg-games/rws-game-rules--non-commission-baccarat-with-super-six-(etg).pdf
        List<Integer> playerHand = new ArrayList<>();
        List<Integer> brokerHand = new ArrayList<>();
        // deal the cards - 2 to player, 2 to broker (alternating)
        playerHand.add(drawConverted());
        brokerHand.add(drawConverted());
        playerHand.add(drawConverted());
        brokerHand.add(drawConverted());
        int playerValue = calcHandValue(playerHand);
        int brokerValue = calcHandValue(brokerHand);
        // check for natural 8 or 9
        if (playerValue < 8 && brokerValue < 8) {
            if (playerValue < 6) {
                int playerThirdCard = drawConverted();
                playerHand.add(playerThirdCard);
                // apply broker draw card rules
                if (brokerValue <= 2) {
                    brokerHand.add(drawConverted());
                } else if (brokerValue == 3) {
                    if (playerThirdCard != 8) {
                        brokerHand.add(drawConverted());
                    }
                } else if (brokerValue == 4) {
                    if (playerThirdCard < 2 || playerThirdCard > 7) {
                        brokerHand.add(drawConverted());
                    }
                } else if (brokerValue == 5) {
                    if (playerThirdCard < 4 || playerThirdCard > 7) {
                        brokerHand.add(drawConverted());
                    }
                } else if (brokerValue == 6) {
                    if (playerThirdCard < 6 || playerThirdCard > 7) {
                        brokerHand.add(drawConverted());
                    }
                } // if broker 7, no need draw
            } else if (brokerValue < 6) {
                brokerHand.add(drawConverted());
            } // 6-6 rule - no more draws
        } // natural 8 or 9 - no more draws
        // print out final hands
        String message = "P";
        for (Integer card : playerHand) {
            message = message + "|" + card;
        }
        message += ",B";
        for (Integer card : brokerHand) {
            message = message + "|" + card;
        }
        return message;
    }

    public int drawConverted() {
        // transform J Q K to 10
        int card = this.bigDeck.drawCard().intValue();
        if (card > 10) {
            card = 10;
        }
        return card;
    }

    public int calcHandValue(List<Integer> hand) {
        int result = 0;
        for (Integer integer : hand) {
            result += integer;
        }
        return result % 10;
    }

}


        // Map<String,List<Integer>> hands = new HashMap<>();
        // for (int i = 0; i < 4; i++) {
        //     currentCard = drawConverted();
        //     if ((i+1) % 2 == 0) {
        //         sides = "P";
        //     } else {
        //         sides = "B";
        //     }
        //     if (hands.keySet().contains(sides)) {
        //         hands.get(sides).add(currentCard);
        //     } else {
        //         List<Integer> values = new ArrayList<>();
        //         values.add(currentCard);
        //         hands.put(sides, values);
        //     }
        // }