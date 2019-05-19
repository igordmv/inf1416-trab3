package Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBControl {

    private static final DBControl instance = new DBControl();
    public static DBControl getInstance() {
        return instance;
    }

    /* **************************************************************************************************
     **
     **  MARK: Log Query
     **
     ****************************************************************************************************/

    public List logQuery() {

        return runQuerySelect("select Registro.id, email, created, texto from Registro JOIN Mensagem ON Mensagem.id = Registro.messageId order by created;");

    }

    /* **************************************************************************************************
     **
     **  MARK: Select All Users Query
     **
     ****************************************************************************************************/

    public List<HashMap<String,Object>> selectAllUsersQuery(){

        return runQuerySelect("SELECT * FROM User");

    }

    /* **************************************************************************************************
     **
     **  MARK: insert Register
     **
     ****************************************************************************************************/

    public boolean insertRegister(int idMsg, String email, String arquivo) {

        return runQueryInsertUpdate(String.format("INSERT INTO Registro (messageId, email, filename) VALUES ('%d', '%s', '%s')", idMsg, email, arquivo));

    }

    /* **************************************************************************************************
     **
     **  MARK: insert Register
     **
     ****************************************************************************************************/

    public boolean insertRegister(int idMsg) {

        return this.insertRegister(idMsg, null, null);

    }

    /* **************************************************************************************************
     **
     **  MARK: insert Register
     **
     ****************************************************************************************************/

    public boolean insertRegister(int idMsg, String email) {

        return this.insertRegister(idMsg, email, null);

    }

    /* **************************************************************************************************
     **
     **  MARK: clear Wrong Access Password
     **
     ****************************************************************************************************/

    public void clearWrongAccessPassword(String email) {

        runQueryInsertUpdate(String.format("UPDATE User SET numberWrongAccessPassword = 0 WHERE email = '%s'", email));

    }

    /* **************************************************************************************************
     **
     **  MARK: clear Wrong Access Private Key
     **
     ****************************************************************************************************/

    public void clearWrongAccessPrivateKey(String email) {

        runQueryInsertUpdate(String.format("UPDATE User SET numberWrongAccessPrivateKey = 0 WHERE email = '%s'", email));

    }

    /* **************************************************************************************************
     **
     **  MARK: Increase Wrong Access Private Key
     **
     ****************************************************************************************************/

    public void incrementWrongAccessPrivateKey(String email) {

        runQueryInsertUpdate(String.format("UPDATE User SET numberWrongAccessPrivateKey = numberWrongAccessPrivateKey + 1, lastTryWrongAcess = datetime('now') WHERE email = '%s'", email));

    }

    /* **************************************************************************************************
     **
     **  MARK: Increase Wrong Access Password
     **
     ****************************************************************************************************/

    public void incrementWrongAccessPassword(String email) {

        runQueryInsertUpdate(String.format("UPDATE User SET numberWrongAccessPassword = numberWrongAccessPassword + 1, lastTryWrongAcess = datetime('now') WHERE email = '%s'", email));

    }

    /* **************************************************************************************************
     **
     **  MARK: Run SQL File
     **
     ****************************************************************************************************/

    public void runSqlFile() {

        System.out.println("OII");

        String allSql = "";

        try{
            BufferedReader br = new BufferedReader(new FileReader("trabalho3.sql"));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                allSql = allSql + strLine.replace("\uFEFF", "") + "";
            }
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println(allSql);

        runQueryInsertUpdate(allSql);

        System.out.println("runSqlFile - ok");

    }

    /* **************************************************************************************************
     **
     **  MARK: Connect
     **
     ****************************************************************************************************/

    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /* **************************************************************************************************
     **
     **  MARK: Run Query Insert/Update
     **
     ****************************************************************************************************/

    private boolean runQueryInsertUpdate(String query) {
        Connection conn = connect();
        try {
            Statement stat = conn.createStatement();
            stat.setQueryTimeout(30);
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        closeConnection(conn);
        return true;
    }

    /* **************************************************************************************************
     **
     **  MARK: Run Query Select
     **
     ****************************************************************************************************/

    private List<HashMap<String,Object>> runQuerySelect(String query) {
        Connection conn = connect();
        try {
            Statement stat = conn.createStatement();
            stat.setQueryTimeout(30);
            ResultSet res = stat.executeQuery(query);
            List<HashMap<String,Object>> lst = convertResultSetToList(res);
            stat.close();
            closeConnection(conn);
            return lst;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection(conn);
            return null;
        }
    }

    /* **************************************************************************************************
     **
     **  MARK: Convert Result To List
     **
     ****************************************************************************************************/

    private List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

        while (rs.next()) {
            HashMap<String,Object> row = new HashMap<String, Object>(columns);
            for(int i=1; i<=columns; ++i) {
                row.put(md.getColumnName(i),rs.getObject(i));
            }
            list.add(row);
        }

        return list;
    }

    /* **************************************************************************************************
     **
     **  MARK: Close Connection
     **
     ****************************************************************************************************/

    private void closeConnection(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        }
        catch (SQLException e) {
            System.err.println(e);
        }
    }


}
