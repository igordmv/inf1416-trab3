package Database;

import Auth.Authentification;

import java.util.HashMap;

public class LoggedUser {

    /* **************************************************************************************************
     **
     **  Variables desclaration
     **
     ****************************************************************************************************/

    private String email = null;

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

        return Authentification.autenticaEmail(this.email);

    }

    public String getEmail() {

        return this.email;

    }

    public void setEmail(String email) {
        this.email = email;
    }

}
