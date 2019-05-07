import View.LoginView;

import java.util.HashMap;
import java.util.List;

public class main {

    public static void main (String[] args){
        DBManager.insereRegistro(1001);
       List<HashMap<String,Object>> list = DBManager.getAllMessages();
        System.out.println(list);
       for(HashMap<String,Object> hash : list){
           System.out.println(hash);
         System.out.println(hash.get("text"));
       }
    }
}
