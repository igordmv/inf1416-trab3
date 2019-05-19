package Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBManager {
	
///////////////////////////////////////////////////
// 
//                Public methods
//_________________________________________________
	
	public static Connection connect() {
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

	public static List<HashMap<String,Object>> getAllMessages(){
		return selectFromDb("select texto from Mensagem");
	}

	public static boolean addUser(String name, String email, String group, String salt, String senha, String certDig) {
		return insertIntoDb(String.format("INSERT INTO User VALUES "
				+ "('%s', '%s', '%s', '%s', '%s', 1, 0, null, 0, 0, '%s', 0, 0)"
				, name, email, group, salt, senha, certDig)
			);
	}

	public static boolean insereRegistro(int idMsg) {
		return insereRegistro(idMsg, null, null);
	}
	
	public static boolean insereRegistro(int idMsg, String email) {
		return insereRegistro(idMsg, email, null);
	}
	
	public static boolean insereRegistro(int idMsg, String email, String arquivo) {
		return insertIntoDb(String.format("INSERT INTO Registro (messageId, email, filename) VALUES ('%d', '%s', '%s')", idMsg, email, arquivo));
	}

	public static int retornaNumUsuarios() {
		return selectFromDb(String.format("SELECT * FROM User")).size();
	}

	public static List getUser(String email) throws ClassNotFoundException {
		return selectFromDb(String.format("SELECT * FROM User WHERE email = '%s'", email));
	}

	public static List<HashMap<String,Object>> getUsers(){
		return selectFromDb("SELECT * FROM User");
	}

	public static void alterarCertificadoDigital(String certificado, String email) {
		updateDb(String.format("UPDATE User SET certificate = '%s' WHERE email = '%s'", certificado, email));
	}
	
	public static void alterarSenha(String novaSenha, String email) {
		updateDb(String.format("UPDATE User SET passwordDigest = '%s' WHERE email = '%s'", novaSenha, email));
	}

	
///////////////////////////////////////////////////
//	
//                Private methods 
//_________________________________________________
	
	private static boolean insertIntoDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			stat.executeUpdate(query);
			stat.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			closeConn(conn);
			return false;
		}
		closeConn(conn);
		return true;
	}
	
	private static void updateDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			stat.executeUpdate(query);
			stat.close();			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		closeConn(conn);
	}
	
	private static List<HashMap<String,Object>> selectFromDb(String query) {
		Connection conn = connect();
		try {
			Statement stat = conn.createStatement();
			stat.setQueryTimeout(30);
			ResultSet res = stat.executeQuery(query);
			List<HashMap<String,Object>> lst = convertResultSetToList(res);
 			stat.close();
			closeConn(conn);
			return lst;
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			closeConn(conn);
			return null;
		}
	}
	
	private static boolean closeConn(Connection conn) {
		try {
			if (conn != null) 
				conn.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			return false;
		}
		return true;
	}
	
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

}
