package server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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

    @Override
    public void run() {
        System.out.println("Starting a client thread");
        NetworkIO netIO = null;
        DBHandler db = new DBHandler();
        boolean gameOn = true;

        // assume 1 round of decks first. play till deck runs out, hence not in while
        // loop
        try {
            db.generateDeck(numOfDecks);
            db.shuffleDeck();
            netIO = new NetworkIO(this.socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // game loop
        // keep listening for client commands
        while (gameOn) {
            try {
                if (db.deckSize() < 6) {
                    gameOn = false;
                    break; // break out of game immediately
                }
                System.out.println("Listening for client commands...");
                String clientRequest = netIO.read();
                System.out.printf("[client] %s\n", clientRequest);
                // client command will come in as "username " + command
                String[] command = clientRequest.toLowerCase().split(" ");
                String message = "";

                // check for valid command lengths (enough arguments for future methods)
                if (command.length == 1 ||
                        command[1].equalsIgnoreCase("exit") && command.length < 2 ||
                        command[1].equalsIgnoreCase("withdraw") && command.length < 2 ||
                        command[1].equalsIgnoreCase("login") && command.length < 3 ||
                        command[1].equalsIgnoreCase("bet") && command.length < 3 ||
                        command[1].equalsIgnoreCase("deal") && command.length < 3) {
                    message = "Please enter a valid command";
                }
                // check to ensure login is first command
                else if (command[0].equals("0") && !command[1].equals(LOGIN)) {
                    message = "Please login first!";
                }
                // process valid commands
                else {
                    switch (command[1]) {
                        case LOGIN:
                            int buyIn = 0;
                            if (command.length > 3) {
                                buyIn = Integer.parseInt(command[3]);
                            }
                            db.loginAddWallet(command[2], buyIn);
                            message = "Login " + command[2] + " successful. Wallet has $" + db.checkWallet(command[2]);
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
                            // send the hand to client, and read response
                            message = db.deal(); // "P|X|X|X,B|Y|Y|Y"
                            System.out.println(message);
                            netIO.write(message);
                            clientRequest = netIO.read(); // "B wins with 6 points" "Draw with 7 points"
                            // parse msg to determine winner and allocate bet + generate message to send
                            // back to client
                            String sideChosen = command[2];
                            float multiplier = winningMultiplier(sideChosen, clientRequest);
                            int winnings = (int) (db.retrieveBet(command[0]) * multiplier); // round down if decimal
                            db.loginAddWallet(command[0], winnings);
                            db.flushBets(); // clear current session bets
                            message = "Your current wallet is $" + String.valueOf(db.checkWallet(command[0]));
                            break;
                        case WITHDRAW:
                            int amount = db.withdrawWallet(command[0]);
                            System.out.println(amount);
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
                System.out.println(message);
                netIO.write(message); // send one msg back per client command
                System.out.println("Executed client command!");
            } catch (SocketException se) {
                se.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    netIO.write("Error");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        netIO.close();
    }

    public static float winningMultiplier(String sideChosen, String gameResults) {
        String winner = String.valueOf(gameResults.charAt(0));

        if (winner.equalsIgnoreCase("D")) {
            return 1;
        } else if (sideChosen.equalsIgnoreCase("B") && gameResults.equalsIgnoreCase("B wins with 6 points")) {
            return 1.5f;
        } else if (sideChosen.equalsIgnoreCase(winner)) {
            return 2;
        } else {
            return 0;
        }
    }

}