package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientApp {
    public static void main(String[] args) {
        
        // String playerName = "";
        
        // default argument values
        String host = "localhost";
        int port = 3000;

        // override with command line arguments
        if (args.length > 0) {
            String[] hostPort = args[0].split(":");
            host = hostPort[0];
            port = Integer.parseInt(hostPort[1]);
        }

        try (Socket socket = new Socket(host,port)) {
            System.out.println("Connected to server.");
            // prompt to send command

            // if command begins with "Login", save args[1] as playerName (overwrite!)
            // all msges written to server will begin with playerName + " " + msg

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
