package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHandler {
    private File usersDB = new File("usersDB"); // default!! hard-coded
    private File cardsDB = new File("cards.db"); // default!! hard-coded
    private File currentSessionBets = new File("bets.db"); // default!! hard-coded

    public DBHandler() {
    }

    public void writeDB(File path, List<String> contents) throws IOException {
        OutputStream os = new FileOutputStream(path);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);
        for (String line : contents) {
            bw.write(line);
            bw.newLine();
        }
        bw.flush();
        writer.flush();
        os.flush();
        bw.close();
        writer.close();
        os.close();
    }

    public void writeDB(File path, String contents) throws IOException {
        OutputStream os = new FileOutputStream(path);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(contents);
        bw.flush();
        writer.flush();
        os.flush();
        bw.close();
        writer.close();
        os.close();
    }

    public List<String> readDB(File path) throws IOException {
        List<String> message = new ArrayList<>();
        String line = "";
        InputStream is = new FileInputStream(path);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        while ((line = br.readLine()) != null) {
            message.add(line);
        }
        br.close();
        reader.close();
        is.close();
        return message;
    }

    public void generateDeck(int numOfDecks) throws IOException {
        List<String> deck = new ArrayList<>();
        int[] value = {1,2,3,4,5,6,7,8,9,10,11,12,13};
        int[] suits = {1,2,3,4};
        for (int i = 0; i < numOfDecks; i++) {
            for (int suit : suits) {
                for (int val : value) {
                    deck.add(val + "." + suit);
                }
            }
        }
        writeDB(cardsDB, deck);
    }

    public void shuffleDeck() throws IOException {
        List<String> deck = readDB(cardsDB);
        Collections.shuffle(deck);
        writeDB(cardsDB, deck);
    }

    private String drawCard() throws IOException {
        List<String> deck = readDB(cardsDB);
        String card = deck.get(0);
        deck.remove(0);
        writeDB(cardsDB, deck);
        return card;
    }

    public int drawConvertedCard() throws IOException {
        int card = (int)Float.parseFloat(drawCard());
        if (card > 10) {
            card = 10;
        }
        return card;
    }

    private File userFile(String username) {
        return new File(usersDB.getPath() + File.separator + username.toLowerCase() + ".db");
    }

    public void loginAddWallet(String username, int amount) throws IOException {
        if (!userFile(username).exists()) {
            userFile(username).createNewFile();
        } else {
            // top up value
            amount += checkWallet(username);
        }
        writeDB(userFile(username), String.valueOf(amount));
    }

    public int withdrawWallet(String username) throws IOException {
        File userFile = userFile(username);
        List<String> wallet = readDB(userFile);
        int amount = Integer.parseInt(wallet.get(0));
        wallet.add(0,"0");
        writeDB(userFile, wallet);
        return amount;
    }

    public int checkWallet(String username) throws IOException {
        File userFile = userFile(username);
        return Integer.parseInt(readDB(userFile).get(0));
    }

    public void bet(String username, int amount) throws IOException {
        // deduct from player's wallet in usersDB
        File userFile = userFile(username);
        List<String> wallet = readDB(userFile);
        int newWallet = Integer.parseInt(wallet.get(0)) - amount;
        wallet.add(0, String.valueOf(newWallet));
        // record in current session's bets.db
        writeDB(currentSessionBets, username + "," + String.valueOf(amount) + "\n");
    }

    public int retrieveBet(String username) throws IOException {
        int amount = 0;
        List<String> bets = readDB(currentSessionBets);
        for (String bet : bets) {
            String[] userBet = bet.split(",");
            if (userBet[0].equals(username)) {
                amount = Integer.parseInt(userBet[1]);
            }
        }
        return amount;
    }

    public String deal() throws IOException {
        // https://www.gra.gov.sg/docs/default-source/game-rules/rws/semi-automated-etg-games/rws-game-rules--non-commission-baccarat-with-super-six-(etg).pdf
        List<Integer> playerHand = new ArrayList<>();
        List<Integer> brokerHand = new ArrayList<>();
        // deal the cards - 2 to player, 2 to broker (alternating)
        playerHand.add(drawConvertedCard());
        brokerHand.add(drawConvertedCard());
        playerHand.add(drawConvertedCard());
        brokerHand.add(drawConvertedCard());
        int playerValue = calcHandValue(playerHand);
        int brokerValue = calcHandValue(brokerHand);
        // check for natural 8 or 9
        if (playerValue < 8 && brokerValue < 8) {
            if (playerValue < 6) {
                int playerThirdCard = drawConvertedCard();
                playerHand.add(playerThirdCard);
                // apply broker draw card rules
                if (brokerValue <= 2) {
                    brokerHand.add(drawConvertedCard());
                } else if (brokerValue == 3) {
                    if (playerThirdCard != 8) {
                        brokerHand.add(drawConvertedCard());
                    }
                } else if (brokerValue == 4) {
                    if (playerThirdCard < 2 || playerThirdCard > 7) {
                        brokerHand.add(drawConvertedCard());
                    }
                } else if (brokerValue == 5) {
                    if (playerThirdCard < 4 || playerThirdCard > 7) {
                        brokerHand.add(drawConvertedCard());
                    }
                } else if (brokerValue == 6) {
                    if (playerThirdCard < 6 || playerThirdCard > 7) {
                        brokerHand.add(drawConvertedCard());
                    }
                } // if broker 7, no need draw
            } else if (brokerValue < 6) {
                brokerHand.add(drawConvertedCard());
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

    public int calcHandValue(List<Integer> hand) {
        int result = 0;
        for (Integer integer : hand) {
            result += integer;
        }
        return result % 10;
    }

}