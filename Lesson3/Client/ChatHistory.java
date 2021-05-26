package Client;

import java.io.*;
import java.util.ArrayList;

public class ChatHistory {
    public static PrintWriter out;

    public static void startWriting(String login, String text) throws IOException {
        try {
//          PrintWriter out = new PrintWriter("history/history_" + login + ".txt", "UTF-8");
            out = new PrintWriter(new FileOutputStream("history/history_" + login + ".txt", true), true);
            out.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void closeWriting() {
        out.close();
    }

    public static ArrayList<String> readHistory(String login) throws IOException {
        ArrayList<String> strList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("history/history_" + login + ".txt"))) {
            String str;
            while ((str = reader.readLine()) != null) {
                strList.add(str);
            }
            if (strList.size() <= 100) {
                return strList;
            } else {
                ArrayList<String> strList100 = new ArrayList<>();
                for (int i = strList.size() - 100; i < strList.size() ; i++) {
                    strList100.add(strList.get(i));
                    return strList100;
                }
            }
            return strList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            strList = null;
            return strList;
        }

    }

}


