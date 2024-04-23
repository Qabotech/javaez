import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<PrintWriter> clientOutputStreams;

    public Server(int port, int maxThreads) {
        try {
            serverSocket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(maxThreads);
            clientOutputStreams = new ArrayList<>();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                System.out.println("Enter your name:");
                String userName = in.readLine(); // Read user's name from client

                // Create a User object with the provided name
                User user = new User(userName);

                clientOutputStreams.add(out);

                ClientHandler clientHandler = new ClientHandler(clientSocket, out, this, user);
                executorService.submit(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendMessageToClientAtIndex(int clientIndex, String message) {
        if (clientIndex >= 0 && clientIndex < clientOutputStreams.size()) {
            PrintWriter client = clientOutputStreams.get(clientIndex);
            client.println("Server: " + message);
        } else {
            System.out.println("Invalid client index.");
        }
    }



    public void broadcastMessage(String message, PrintWriter sender, User user) {
        for (PrintWriter client : clientOutputStreams) {
            if (client != sender) {
                client.println(user.getName() + ": " + message);
            }
        }
    }



    public static void main(String[] args) {
        int port = 8080;
        int maxThreads = 10;

        Server server = new Server(port, maxThreads);
        server.start();
    }
}
