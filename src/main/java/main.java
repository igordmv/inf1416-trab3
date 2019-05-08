import Database.DBControl;

public class main {

    public static void main (String[] args){

//        Database.DBControl.getInstance().runSqlFile();

        System.out.println(DBControl.getInstance().logQuery());

//        DBManager.insereRegistro(1001);
//       List<HashMap<String,Object>> list = DBManager.getAllMessages();
//        System.out.println(list);
//       for(HashMap<String,Object> hash : list){
//           System.out.println(hash);
//         System.out.println(hash.get("text"));
//       }
    }
}
