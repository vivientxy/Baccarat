package client;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

                switch (command.split(" ")[1].toLowerCase()) {
                    case LOGIN:
                        playerName = command.split(" ")[2]; // overwrite current session username
                        break;
                    case EXIT:
                        gameOn = false;
                        break;
                    default:
                        break;
                }

                dos.writeUTF(command);
                dos.flush();
                dis.readUTF();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }
}
