import Database.DBControl;

import java.util.HashMap;
import java.util.List;

public class LogMain {

    public static void main(String[] args) {
        List<HashMap> logs = DBControl.getInstance().logQuery();

        for (HashMap log: logs) {
            String user = (String)log.get("email");
            String file = (String)log.get("filename");
//            System.out.println(log);
            if (user.equals("null")) {
                System.out.println(String.format("id: %s | date: %s | text: %s", (Integer)log.get("id"), (String)log.get("created"), (String)log.get("texto")));
            }
            else {

                if(file == null) {
                    String texto = (String) log.get("texto");
                    texto = texto.replaceAll("<login_name>", user);
                    System.out.println(String.format("id: %s | date: %s | user: %s | text: %s", (Integer) log.get("id"), (String) log.get("created"), user, texto));

                } else {
                    String texto = (String)log.get("texto");
                    texto = texto.replaceAll("<login_name>",user);
                    texto = texto.replaceAll("<arq_name>",file);
                    System.out.println(String.format("id: %s | date: %s | user: %s | text: %s", (Integer)log.get("id"), (String)log.get("created"), user, texto));
                }
            }
        }
    }

}
