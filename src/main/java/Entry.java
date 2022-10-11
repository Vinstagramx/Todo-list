import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 */
public class Entry {

    private Integer itemID;
    private String content;
    private String createdDate;
    private static int counter; // TODO: Pull max id from sql database and then use start counter from there

    Entry(Integer itemID, String content, String createdDate){
        this.itemID = itemID;
        this.content = content;
        this.createdDate = createdDate;
    }
    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
//    private Connection connect() {
//        // SQLite connection string
//        String url = "jdbc:sqlite:C:/code/training/intro-to-java/Training-todo/src/main/sqlite/db/todo.db";
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(url);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return conn;
//    }

    /**
     * Insert a new row into the warehouses table
     */
    public void insert(Connection conn) {
        String sql = "INSERT INTO todo_list(item_id,content,created_date) VALUES(?,?,?)";

        try (
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.itemID);
            pstmt.setString(2, this.content);
            pstmt.setString(3, this.createdDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//        NewEntry app = new NewEntry();
//        // insert three new rows
//        app.insert("Raw Materials", 3000);
//        app.insert("Semifinished Goods", 4000);
//        app.insert("Finished Goods", 5000);
//    }

}
