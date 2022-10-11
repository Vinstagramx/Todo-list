import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    public Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:/code/training/intro-to-java/Training-todo/src/main/sqlite/db/todo.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void delete(int id) {
        String sql = "DELETE FROM todo_list WHERE item_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
