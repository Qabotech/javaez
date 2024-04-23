import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        try {
            Socket socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your name:");
            String userName = consoleInput.readLine(); // Read user's name

            out.println(userName); // Send the name to the server

            // Create a User object with the provided name
            User user = new User(userName);

            // Start a new thread to listen for broadcast messages
            new Thread(() -> {
                try {
                    String broadcastMessage;
                    while ((broadcastMessage = in.readLine()) != null) {
                        System.out.println("Broadcast: " + broadcastMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInput;

            System.out.println("Enter message to send to server (type 'exit' to quit):");
            while ((userInput = consoleInput.readLine()) != null) {
                System.out.println("Send as broadcast? (Y/N):");
                String choice = consoleInput.readLine();
                if (choice.equalsIgnoreCase("Y")) {
                    out.println(userInput); // Send the message to the server as a broadcast
                } else if (choice.equalsIgnoreCase("N")) {
                    System.out.println("Enter recipient index:");
                    int recipientIndex = Integer.parseInt(consoleInput.readLine());
                    out.println("sendto " + recipientIndex + " " + userInput); // Send the message to the server for a specific person
                } else {
                    System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
                    continue; // Repeat the loop to prompt again
                }

                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.println("Enter message to send to server (type 'exit' to quit):");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
