import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private Server server;
    private User user;

    public ClientHandler(Socket clientSocket, PrintWriter out, Server server, User user) {
        this.clientSocket = clientSocket;
        this.out = out;
        this.server = server;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client " + user.getName() + ": " + inputLine);
                if (inputLine.startsWith("sendto")) {
                    String[] parts = inputLine.split(" ");
                    if (parts.length >= 3) {
                        try {
                            int clientIndex = Integer.parseInt(parts[1]);
                            String message = parts[2];
                            server.sendMessageToClientAtIndex(clientIndex, message);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid client index format.");
                        }
                    } else {
                        System.out.println("Invalid sendto command format.");
                    }
                } else {
                    server.broadcastMessage(inputLine, out, user);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
