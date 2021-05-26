package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Controller {

    @FXML
    TextField msgField, loginField, passwordField, nicknameField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, msgPanel;

    @FXML
    ListView<String> clientList;

    @FXML
    Button logoutButton, loginButton, regButton;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private static  String login;

    public void sendMsg(javafx.event.ActionEvent actionEvent) {
        String msg = msgField.getText() + "\n";
        try {
            out.writeUTF(msg);
            msgField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to send message.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void login(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() || nicknameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Empty fields are not allowed.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            out.writeUTF("/login " + loginField.getText() + " " + passwordField.getText() + " " + nicknameField.getText());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void regNewUser(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() || nicknameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Empty fields are not allowed.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            out.writeUTF("/reg " + loginField.getText() + " " + passwordField.getText() + " " + nicknameField.getText());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
            logoutButton.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            logoutButton.setManaged(false);
            clientList.getItems().clear();
            msgArea.clear();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread dataThread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")) {
                            setUsername(msg.split("\\s")[1]);
                            login = msg.split("\\s")[2];
                            /*!!!!!!!!!*/
                            historyLoad(ChatHistory.readHistory(login), msgArea);
                            /*!!!!!!!!!*/
                            break;
                        }
                        if (msg.startsWith("/login_failed ")) {
                            String cause = msg.split("\\s", 2)[1];
                            msgArea.appendText(cause + '\n');
                        }
                        if (msg.startsWith("/login_occupied ")) {
                            msgArea.appendText("This login is already occupied!" + '\n');
                        }
                        if (msg.startsWith("/nickname_occupied ")) {
                            msgArea.appendText("This nickname is already occupied!" + '\n');
                        }
                        if (msg.startsWith("/incorrect ")) {
                            msgArea.appendText("Login/password is incorrect!" + '\n');
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            executeCommand(msg);
                            continue;
                        }
                        msgArea.appendText(msg);
                        /*!!!!!!!!!!!!!!!!!!!!!!!!!*/
                        ChatHistory.startWriting(getLogin(), msg);
                        ChatHistory.closeWriting();
                        /*!!!!!!!!!!!!!!!!!!!!!!!!!*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            dataThread.start();

        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to server [localhost: 8189]");
        }
    }

    private void executeCommand(String cmd) {
        if (cmd.startsWith("/clients_list ")) {
            String[] tokens = cmd.split("\\s");

            Platform.runLater(() -> {
                clientList.getItems().clear();
                for (int i = 1; i < tokens.length; i++) {
                    clientList.getItems().add(tokens[i]);
                }
            });
        }

        if (cmd.startsWith("/logout")) {
            clientList.getItems().clear();
            msgArea.clear();
            disconnect();
        }
    }

    public void disconnect() {
        setUsername(null);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void logout() {
        try {
            out.writeUTF("/logout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLogin() {
        return login;
    }

    private static void historyLoad(ArrayList<String> chatHistory, TextArea msgArea) {
        if (chatHistory != null) {
            for (int i = 0; i < chatHistory.size(); i++) {
                msgArea.appendText(chatHistory.get(i) + '\n');
            }
            msgArea.appendText("Chat history loaded successfully!" + '\n');
        }
    }

}


