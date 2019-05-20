package Database;

import Util.AccessFileFunctions;

import java.security.PrivateKey;
import java.util.HashMap;

public class LoggedUser {

    /* **************************************************************************************************
     **
     **  Variables desclaration
     **
     ****************************************************************************************************/

    private String email = null;

    private static final LoggedUser instance = new LoggedUser();

    private static PrivateKey privateKey = null;

    private static String secretWord = null;

    /* **************************************************************************************************
     **
     **  Get and Set
     **
     ****************************************************************************************************/

    public static LoggedUser getInstance() {
        return instance;
    }

    public static String getSecretWord() {
        return secretWord;
    }

    public static void setSecretWord(String secretWord) {
        LoggedUser.secretWord = secretWord;
    }

    public HashMap getUser() {

        return AccessFileFunctions.checkEmail(this.email);

    }

    public String getEmail() {

        return this.email;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static PrivateKey getPrivateKey() {
        return LoggedUser.getInstance().privateKey;
    }

    public static void setPrivateKey(PrivateKey privateKey) {
        LoggedUser.getInstance().privateKey = privateKey;
    }

}
