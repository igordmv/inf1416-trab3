import Database.DBControl;
import View.LoginView;
import View.MensagemType;

import java.util.HashMap;
import java.util.List;

public class main {

    public static void main (String[] args){

        DBControl.getInstance().insertRegister(MensagemType.SISTEMA_INICIADO, null, null);

        List<HashMap<String, Object>> list = DBControl.getInstance().selectAllUsersQuery();

        for(HashMap<String,Object> hash : list) {

            System.out.println(hash);
            System.out.println(hash.get("text"));

        }

        new LoginView();

    }
}
