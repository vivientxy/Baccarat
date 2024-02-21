package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    public static void main(String[] args) throws IOException {

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

        System.out.printf( "Server App started at %s\n", port);
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        boolean serverOn = true;

        try (ServerSocket server = new ServerSocket(port)) {
            while (serverOn) {
                // keep listening for client connection
                System.out.println("Waiting for client connection..."); 
                Socket socket = server.accept();
                System.out.println("Connected!");
                BaccaratEngine engine = new BaccaratEngine(socket, numOfDecks); // this is the game loop!
                threadPool.submit(engine);
                System.out.println("Submitted to threadpool");
                socket.close(); // close the socket! if not server will accept connections on another socket but not release this socket for use
            }
        }
    }
}
