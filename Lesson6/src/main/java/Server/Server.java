package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/*!!!!!!!!!!*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        try {
            AuthorizationService.connection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server is running at port " + port + "...");
//            System.out.printf("Server is running at port %s...%n", port);
            while (true) {
                LOGGER.info("Waiting for new client...");
//                System.out.println("Waiting for new client...");
                Socket socket = serverSocket.accept();
                LOGGER.info("Client connected successfully!");
//                System.out.println("Client connected successfully!");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            AuthorizationService.disconnect();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        broadcastMessage("User " + clientHandler.getUsername() + " is online \n");
        broadcastClientList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        broadcastMessage("User " + clientHandler.getUsername() + " is offline \n");
        broadcastClientList();
    }

    public synchronized void broadcastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(receiverUsername)) {
                client.sendMessage("From: " + sender.getUsername() + " Message: " + message);
                sender.sendMessage("To: " + receiverUsername + " Message: " + message);
                return;
            }
        }
        sender.sendMessage("Unable to send message to: " + receiverUsername + ". User not found.");
    }

//    public synchronized boolean isUserOnline(String nickname) {
//        for (ClientHandler clientHandler : clients) {
//            if(clientHandler.getUsername().equals(nickname)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void broadcastClientList() throws IOException {
        StringBuilder stringBuilder = new StringBuilder("/clients_list ");
        for (ClientHandler client : clients) {
            stringBuilder.append(client.getUsername()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String clientsList = stringBuilder.toString();
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(clientsList);
        }
    }

    public synchronized ArrayList<String> getOnlineNicknames() {
        ArrayList<String> onlineNicknames = new ArrayList<>();
        for (ClientHandler clientHandler : clients) {
            onlineNicknames.add(clientHandler.getUsername());
        }
        return onlineNicknames;
    }


}
