package Server;

import java.sql.*;
import java.util.ArrayList;

public class AuthorizationService {
    private static Connection connection;
    private static Statement statement;

    static void connection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main_chat.db");
            statement = connection.createStatement();
            System.out.println("DB connection: ok");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String userInfo) throws SQLException {
        String[] tokens = userInfo.split("\\s");
        String sql = String.format("INSERT INTO Users (login, password, nickname) VALUES ('%s', '%s', '%s')",
                tokens[1], tokens[2], tokens[3]);
        try {
            boolean rs = statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInDb(String userInfo, int columnIndex) throws SQLException {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM Users");
            while (rs.next()) {
                String strFromDb = rs.getString(columnIndex);
                if (strFromDb.equals(userInfo)) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean isLoginOnline(ArrayList<String> onlineNicknames, String loginFromClient) {
        for (String onlineNickname : onlineNicknames) {
            String sql = String.format("SELECT login FROM Users WHERE nickname = '%s'", onlineNickname);
            try {
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    String loginFromNickname = rs.getString(1);
                    if (loginFromNickname.equals(loginFromClient)) {
                        return true;
                    }
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    public static boolean checkLoginAndPass(String loginFromClient, String passFromClient) {
        String sql = String.format("SELECT login, password FROM Users");
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String loginFromDB = rs.getString(1);
                String passFromDB = rs.getString(2);
                if (loginFromDB.equals(loginFromClient) && passFromDB.equals(passFromClient)) {
                    return true;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static String getNicknameFromDB(String login) {
        String sql = String.format("SELECT nickname FROM Users WHERE login = '%s'", login);
        try {
            ResultSet rs = statement.executeQuery(sql);
            return rs.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void changeNickname(String oldNickname, String newNickname) {
        String sql = String.format("UPDATE Users SET nickname = '%s' WHERE nickname = '%s'", newNickname, oldNickname);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
