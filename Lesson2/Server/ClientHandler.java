package Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        executeCommand(msg);
                    } else
                        server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    disconnect();
                    authorizationService.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            if (authorizationService.isInDb(loginFromClient, 2)) {
                sendMessage("/login_occupied ");
            } else {
                if (authorizationService.isInDb(nicknameFromClient, 4)) {
                    sendMessage("/nickname_occupied ");
                } else {
                    authorizationService.addUser(cmd);
                    username = nicknameFromClient;
                    sendMessage("/login_ok " + username);
                    server.subscribe(this);
                }
            }
        }

        if (cmd.startsWith("/login ")) {
            String loginFromClient = cmd.split("\\s")[1];
            String passFromClient = cmd.split("\\s")[2];
            String nicknameFromClient = cmd.split("\\s")[3];
            if (authorizationService.isLoginOnline(server.getOnlineNicknames(), loginFromClient)) {
                sendMessage("/login_failed This login is online.");
            } else {
                if (!authorizationService.checkLoginAndPass(loginFromClient, passFromClient)) {
                    sendMessage("/incorrect ");
                } else {
                    String previousNickname = authorizationService.getNicknameFromDB(loginFromClient);
                    if (previousNickname.equals(nicknameFromClient)) {
                        username = nicknameFromClient;
                        sendMessage("/login_ok " + username);
                        server.subscribe(this);
                    } else {
                        if (authorizationService.isInDb(nicknameFromClient, 4)) {
                            sendMessage("/nickname_occupied ");
                        } else {
                            authorizationService.changeNickname(previousNickname, nicknameFromClient);
                            username = nicknameFromClient;
                            sendMessage("/login_ok " + username);
                            server.subscribe(this);
                        }
                    }
                }
            }
        }

        if (cmd.startsWith("/change_nick ")) {
            String newNickname = cmd.split("\\s")[1];
            String previousNickname = getUsername();
            if (authorizationService.isInDb(newNickname, 4)) {
                sendMessage("/nickname_occupied ");
            } else {
                server.unsubscribe(this);
                authorizationService.changeNickname(previousNickname, newNickname);
                username = newNickname;
                server.subscribe(this);
            }


        }

        if (cmd.startsWith("/who_am_i")) {
            sendMessage("Your nickname: " + getUsername());
        }

        if (cmd.startsWith("/logout")) {
            sendMessage("/logout");
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
