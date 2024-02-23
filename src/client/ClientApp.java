package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientApp {
    private static final String LOGIN = "login";
    private static final String DEAL = "deal";
    private static final String EXIT = "exit";

    public static void main(String[] args) {

        String playerName = "0"; // placeholder value
        String host = "localhost"; // default argument values
        int port = 3000; // default argument values

        // override with command line arguments
        if (args.length > 0) {
            String[] hostPort = args[0].split(":");
            host = hostPort[0];
            port = Integer.parseInt(hostPort[1]);
        }

        InputStream is = null;
        DataInputStream dis = null;
        OutputStream os = null;
        DataOutputStream dos = null;

        try (Socket socket = new Socket(host, port)) { // try with resources will auto-close socket
            System.out.println("Connected to server.");
            Console cons = System.console();
            boolean gameOn = true;

            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            // prompt to send command
            while (gameOn) {
                try {
                    // all msges written to server will begin with playerName + " " + msg
                    String command = playerName + " " + cons.readLine("Send command to server > ");
                    String[] commandSplit = command.split(" ");

                    // check for valid command lengths (enough arguments for future methods)
                    if (commandSplit.length == 1 ||
                            commandSplit[1].equalsIgnoreCase("exit") && commandSplit.length < 2 ||
                            commandSplit[1].equalsIgnoreCase("withdraw") && commandSplit.length < 2 ||
                            commandSplit[1].equalsIgnoreCase("login") && commandSplit.length < 3 ||
                            commandSplit[1].equalsIgnoreCase("bet") && commandSplit.length < 3 ||
                            commandSplit[1].equalsIgnoreCase("deal") && commandSplit.length < 3) {
                        // write to server but don't process the invalid commands
                    } else {
                        switch (commandSplit[1].toLowerCase()) {
                            case LOGIN:
                                playerName = commandSplit[2]; // overwrite current session username
                                break;
                            case EXIT:
                                gameOn = false;
                                break;
                            case DEAL:
                                // write command to server first (unlike other commands)
                                // this enables 1 user input but triggers 2 read 2 writes for both server and client
                                dos.writeUTF(command);
                                dos.flush();
                                // get the hand from server
                                String message = dis.readUTF();
                                System.out.println(message);
                                // parse the hand to send back to server, and write to csv
                                command = getWinner(message);
                                System.out.println(command);
                                writeToCSV(String.valueOf(command.charAt(0)));
                                break;
                            default:
                                break;
                        }
                    }
                    dos.writeUTF(command);
                    dos.flush();
                    // read server response
                    String response = dis.readUTF();
                    System.out.println(response);
                } catch (SocketException se) {
                    System.out.println("Game over!");
                    gameOn = false;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWinner(String hand) {
        String[] playerHand = hand.split(",")[0].split("[|]");
        String[] brokerHand = hand.split(",")[1].split("[|]");
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

        if (playerVal > brokerVal) {
            return "P wins with " + playerVal + " points";
        } else if (playerVal < brokerVal) {
            return "B wins with " + brokerVal + " points";
        } else {
            return "Draw with " + playerVal + " points";
        }
    }

    public static void writeToCSV(String winner) throws IOException {
        // write to CSV
        File csv = new File("game_history.csv");
        if (!csv.exists()) {
            csv.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String line = "";
        String lastLine = null;
        StringBuilder content = new StringBuilder();

        while ((line = br.readLine()) != null) {
            String[] parsedString = line.split(",");
            if (parsedString.length < 5) {
                lastLine = line;
                line += winner + ",";
            } else if (parsedString.length == 5) {
                lastLine = line;
                line += winner + "\n";
            }
            content.append(line).append("\n");
        }
        br.close();

        if (lastLine == null) { // new line of record
            content.append(winner).append(",");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(csv));
        bw.write(content.toString());
        bw.close();
    }
}