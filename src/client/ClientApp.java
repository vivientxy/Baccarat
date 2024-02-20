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

public class ClientApp {
    private static final String LOGIN = "login";
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

        try (Socket socket = new Socket(host,port)) {
            System.out.println("Connected to server.");
            Console cons = System.console();
            boolean gameOn = true;

            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            // prompt to send command
            while (gameOn) {
                // all msges written to server will begin with playerName + " " + msg
                String command = playerName + " " + cons.readLine("Send command to server > ");
                String[] commandSplit = command.split(" ");

                switch (commandSplit[1].toLowerCase()) {
                    case LOGIN:
                        playerName = commandSplit[2]; // overwrite current session username
                        break;
                    case EXIT:
                        gameOn = false;
                        break;
                    default:
                        break;
                }

                dos.writeUTF(command);
                dos.flush();
                // read server response
                String response = dis.readUTF();
                System.out.println(response);

                // if response is P|5|8|6,B|7|10 format, parse it and record winner in csv file
                if (response.contains("[|]")) {
                    String record = "";
                    if (response.contains("won")) {
                        record = commandSplit[2].toUpperCase();
                    } else if (response.contains("lost")) {
                        if (commandSplit[2].equalsIgnoreCase("b")) {
                            record = "P";
                        } else {
                            record = "B";
                        }
                    } else if (response.contains("tie")) {
                        record = "D";
                    }

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
                            line += record + ",";
                        } else if (parsedString.length == 5) {
                            lastLine = line;
                            line += record + "\n";
                        }
                        content.append(line).append("\n");
                    }
                    br.close();
            
                    if (lastLine == null) { // new line of record
                        content.append(record).append(",");
                    }
            
                    BufferedWriter bw = new BufferedWriter(new FileWriter(csv));
                    bw.write(content.toString());
                    bw.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }
}