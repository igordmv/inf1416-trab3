package View;

import Database.DBControl;

import java.util.HashMap;
import java.util.List;

public class LogView {

    public static void main(String[] args) {
        List<HashMap> logs = DBControl.getInstance().logQuery();

        for (HashMap log: logs) {
            String user = (String)log.get("email");
            if (user.equals("null")) {
                System.out.println(String.format("id: %s | date: %s | text: %s", (Integer)log.get("id"), (String)log.get("created"), (String)log.get("texto")));
            }
            else {
                System.out.println(String.format("id: %s | date: %s | user: %s | text: %s", (Integer)log.get("id"), (String)log.get("created"), user, (String)log.get("texto")));
            }
        }
    }

}
