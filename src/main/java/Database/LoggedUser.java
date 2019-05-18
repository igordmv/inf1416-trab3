package Database;

import java.util.HashMap;

public class LoggedUser {

    /* **************************************************************************************************
     **
     **  Variables desclaration
     **
     ****************************************************************************************************/

    private HashMap user = null;

    private static final LoggedUser instance = new LoggedUser();

    /* **************************************************************************************************
     **
     **  Get and Set
     **
     ****************************************************************************************************/

    public static LoggedUser getInstance() {
        return instance;
    }

    public HashMap getUser() {
        return user;
    }

    public void setUser(HashMap user) {
        this.user = user;
    }

}
