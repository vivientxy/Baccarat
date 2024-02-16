package sg.edu.nus.iss.baccarat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main(String[] args) {

        // default argument values
        int port = 3000;
        int numOfDecks = 4;

        // override with command line arguments
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            port = Integer.parseInt(args[0]);
            numOfDecks = Integer.parseInt(args[1]);
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Waiting for client connection...");
            Socket socket = server.accept();
            System.out.println("Connected!");
            // initiate game here with while loop (keep listening for client commands)
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
