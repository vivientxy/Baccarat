package server;

import java.io.IOException;
import java.net.Socket;

public class BaccaratEngine implements Runnable {
    private Socket socket;
    private int numOfDecks;
    private static final String LOGIN = "login";
    private static final String BET = "bet";
    private static final String DEAL = "deal";
    private static final String WITHDRAW = "withdraw";
    private static final String EXIT = "exit";

    public BaccaratEngine(Socket socket, int numOfDecks) throws IOException {
        this.socket = socket;
        this.numOfDecks = numOfDecks;
    }

    // for testing
    public static void main(String[] args) {
        
    }

    @Override
    public void run() {
        System.out.println("Starting a client thread");
        NetworkIO netIO = null;
        DBHandler db = new DBHandler();
        boolean gameOn = true;

        // assume 1 round of decks first. play till deck runs out, hence not in while loop
        try {
            db.generateDeck(numOfDecks);
            netIO = new NetworkIO(this.socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // game loop
        // keep listening for client commands
        while (gameOn) {
            try {
                String clientRequest = netIO.read();
                // client command will come in as "username " + command
                String[] command = clientRequest.toLowerCase().split(" ");
                String message = "Please enter a valid command"; // if empty string passed in
                if (command.length > 0) {
                    if (command[0].equals("0") && command[1] != LOGIN) {
                        message = "Please login first!";
                    } else {
                        switch (command[1]) {
                            case LOGIN:
                                db.loginAddWallet(command[2], Integer.parseInt(command[3]));
                                message = "Login successful";
                                break;
                            case BET:
                                int betAmount = Integer.parseInt(command[2]);
                                int walletAmount = db.checkWallet(command[0]);
                                if (walletAmount < betAmount) {
                                    message = "Insufficient amount";
                                } else {
                                    db.bet(command[0], betAmount);
                                    message = "Bet accepted";
                                }
                                break;
                            case DEAL:
                                message = db.deal();
                                int winnings = db.retrieveBet(command[0]);
                                // parse msg to determine winner
                                switch (results(command[2], message)) {
                                    case "win":
                                        message += "\nCongratulations, you've won the bet!";
                                        winnings += winnings;
                                        break;
                                    case "super6":
                                        message += "\nYou won a super 6! Payout is halved.";
                                        winnings = (int)(winnings * 1.5);   // round down if decimal
                                        break;
                                    case "lose":
                                        message += "\nYou've lost the bet.";
                                        winnings = 0;
                                        break;
                                    case "tie":
                                        message += "\nIt was a tie.";
                                        break;
                                    default:
                                        break;
                                }
                                db.loginAddWallet(command[0], winnings);
                                break;
                            case WITHDRAW:
                                int amount = db.withdrawWallet(command[0]);
                                message = "You have withdrawn $" + amount;
                                break;
                            case EXIT:
                                message = "Thanks for playing!";
                                gameOn = false;
                                break;
                            default:
                                break;
                        }
                    }
                }
                netIO.write(message); // send one msg back per client command
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String results(String bet, String outcome) {
        String[] parseOutcome = outcome.split(",");
        String[] playerHand = parseOutcome[0].split("|");
        String[] brokerHand = parseOutcome[1].split("|");
        int playerVal = 0;
        int brokerVal = 0;
        for (int i = 1; i < playerHand.length; i++) {
            playerVal += Integer.parseInt(playerHand[i]);
        }
        for (int i = 1; i < brokerHand.length; i++) {
            brokerVal += Integer.parseInt(brokerHand[i]);
        }
        playerVal = playerVal % 10;
        brokerVal = brokerVal % 10;
        String results = "";
        if (playerVal == brokerVal) {
            results = "tie";
        } else if (playerVal > brokerVal) {
            if (bet.equalsIgnoreCase("p")) {
                results = "win";
            } else {
                results = "lose";
            }
        } else if (playerVal < brokerVal) {
            if (bet.equalsIgnoreCase("p")) {
                results = "lose";
            } else if (brokerVal == 6) {
                results = "super6";
            } else {
                results = "win";
            }
        }
        return results;
    }

}