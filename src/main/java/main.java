import Database.DBManager;
import View.LoginView;

import java.util.HashMap;
import java.util.List;

public class main {

    public static void main (String[] args){
        DBManager.insereRegistro(MensagemType.SISTEMA_INICIADO);
        List<HashMap<String, Object>> list = DBManager.getUsers();
        for(HashMap<String,Object> hash : list){
            System.out.println(hash);
            System.out.println(hash.get("text"));
        }
        new LoginView();
    }
}
