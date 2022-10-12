import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 *
 */
public class Entry {
// TODO: category and priority
    private Integer itemID;
    private String content;

    private String priority = "Normal";

    private String category = "";
    private String createdDate;

    private static int counter;

    Entry(Integer itemID, String content, String createdDate){
        this.itemID = itemID;
        this.content = content;
        this.createdDate = createdDate;
    }

    /**
     * Inserts the entry into the todo_list table
     */
    public void insert(Connection conn) {
        String sql = "INSERT INTO todo_list(item_id,content,priority,category,created_date) VALUES(?,?,?,?,?)";

        try (
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.itemID);
            pstmt.setString(2, this.content);
            pstmt.setString(3, this.priority);
            pstmt.setString(4, this.category);
            pstmt.setString(5, this.createdDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changePriority(String priority){
        this.priority = priority;
    }

    public void changeCategory(String category){
        this.category = category;
    }

}
