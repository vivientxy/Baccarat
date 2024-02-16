package sg.edu.nus.iss.baccarat.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientApp {
    public static void main(String[] args) {
        
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

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
