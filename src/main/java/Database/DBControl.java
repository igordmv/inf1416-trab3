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
                allSql = allSql + strLine + "  ";
            }
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

        runQueryInsertUpdate(allSql);

        System.out.println("runSqlFile - ok");

//        System.out.println(runQuerySelect("SELECT * FROM Mensagem"));

    }

    /* **************************************************************************************************
     **
     **  MARK: Connect
     **
     ****************************************************************************************************/

    private static Connection connect() {
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

    private static void runQueryInsertUpdate(String query) {
        Connection conn = connect();
        try {
            Statement stat = conn.createStatement();
            stat.setQueryTimeout(30);
            stat.executeUpdate(query);
            stat.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        closeConnection(conn);
    }

    /* **************************************************************************************************
     **
     **  MARK: Run Query Select
     **
     ****************************************************************************************************/

    private static List<HashMap<String,Object>> runQuerySelect(String query) {
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

    private static List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
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

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        }
        catch (SQLException e) {
            System.err.println(e);
        }
    }


}
