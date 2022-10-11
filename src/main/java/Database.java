import java.sql.*;

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

    public void selectAll(){
        String sql = "SELECT item_id, content, created_date FROM todo_list";
        String columnPrintFormat = TerminalScanner.formatStringGenerator(3);

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
             System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Date Created");
            // loop through the result set
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("created_date"));
//                System.out.println(rs.getInt("item_id") +  "\t" +
//                        rs.getString("content") + "\t" +
//                        rs.getString("created_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer findMaxIndex(){
        String sql = "SELECT max(item_id) FROM todo_list";
        Integer maxIndex = null;
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            if (rs.next()) {
                maxIndex = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return maxIndex;
    }

    public void clear() {
        for (int i = 1; i <= this.findMaxIndex(); i++) {
            String sql = "DELETE FROM todo_list WHERE item_id = ?";

            try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // set the corresponding param
                pstmt.setInt(1, i);
                // execute the delete statement
                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
