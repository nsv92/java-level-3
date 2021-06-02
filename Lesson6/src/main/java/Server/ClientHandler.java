package Server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*!!!!!!!!!!*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
//                    LOGGER.info(msg);
//                    System.out.println(msg);
                    if (msg.startsWith("/")) {
                        executeCommand(msg);
                    } else
                        server.broadcastMessage(username + ": " + msg);
                        LOGGER.info(username + ": " + msg);
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    disconnect();
//                    AuthorizationService.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
//        new Thread(() -> {
//            try {
//                while (true) {
//                    String msg = in.readUTF();
//                    System.out.println(msg);
//                    if (msg.startsWith("/")) {
//                        executeCommand(msg);
//                    } else
//                        server.broadcastMessage(username + ": " + msg);
//                }
//            } catch (IOException | SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    disconnect();
////                    AuthorizationService.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }


    private void executeCommand(String cmd) throws IOException, SQLException {
        if (cmd.startsWith("/w ")) {
            String[] tokens = cmd.split("\\s", 3);
            server.sendPrivateMessage(this, tokens[1], tokens[2]);
            return;
        }

        if (cmd.startsWith("/reg ")) {
            String loginFromClient = cmd.split("\\s")[1];
            String nicknameFromClient = cmd.split("\\s")[3];
            if (AuthorizationService.isInDb(loginFromClient, 2)) {
                sendMessage("/login_occupied ");
            } else {
                if (AuthorizationService.isInDb(nicknameFromClient, 4)) {
                    sendMessage("/nickname_occupied ");
                } else {
                    AuthorizationService.addUser(cmd);
                    username = nicknameFromClient;
                    sendMessage("/login_ok " + username + " " + loginFromClient);
                    server.subscribe(this);
                    LOGGER.info(username + ": /login_ok");
                }
            }
        }

        if (cmd.startsWith("/login ")) {
            String loginFromClient = cmd.split("\\s")[1];
            String passFromClient = cmd.split("\\s")[2];
            String nicknameFromClient = cmd.split("\\s")[3];
            if (AuthorizationService.isLoginOnline(server.getOnlineNicknames(), loginFromClient)) {
                sendMessage("/login_failed This login is online.");
            } else {
                if (!AuthorizationService.checkLoginAndPass(loginFromClient, passFromClient)) {
                    sendMessage("/incorrect ");
                } else {
                    String previousNickname = AuthorizationService.getNicknameFromDB(loginFromClient);
                    if (previousNickname.equals(nicknameFromClient)) {
                        username = nicknameFromClient;
                        sendMessage("/login_ok " + username + " " + loginFromClient);
                        server.subscribe(this);
                        LOGGER.info(username + ": /login_ok");
                    } else {
                        if (AuthorizationService.isInDb(nicknameFromClient, 4)) {
                            sendMessage("/nickname_occupied ");
                        } else {
                            AuthorizationService.changeNickname(previousNickname, nicknameFromClient);
                            username = nicknameFromClient;
                            sendMessage("/login_ok " + username + " " + loginFromClient);
                            server.subscribe(this);
                            LOGGER.info(username + ": /login_ok");
                        }
                    }
                }
            }
        }

        if (cmd.startsWith("/change_nick ")) {
            String newNickname = cmd.split("\\s")[1];
            String previousNickname = getUsername();
            if (AuthorizationService.isInDb(newNickname, 4)) {
                sendMessage("/nickname_occupied ");
            } else {
                server.unsubscribe(this);
                AuthorizationService.changeNickname(previousNickname, newNickname);
                username = newNickname;
                server.subscribe(this);
            }


        }

        if (cmd.startsWith("/who_am_i")) {
            sendMessage("Your nickname: " + getUsername());
        }

        if (cmd.startsWith("/logout")) {
            sendMessage("/logout");
            LOGGER.info(username + ": /logout");
        }
    }

    private void disconnect() throws IOException {
        server.unsubscribe(this);
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    public String getUsername() {
        return username;
    }
}
