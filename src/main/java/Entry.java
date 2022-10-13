import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Entry class. Handles all data entries
 * - contains itemID, content, priority and category fields.
 */
public class Entry {

    // itemID, content and createdDate fields must not be blank
    // corresponding fields in database table will always be filled.
    private Integer itemID;
    private String content;
    private String createdDate;

    // priority field defaults to 'Normal' priority if not set by user.
    private String priority = "Normal";

    // category field defaults to '' (blank string) if not set by user.
    private String category = "";


    /**
     * Constructor for Entry class.
     * @param itemID: ID of to-do item.
     * @param content: To-do content.
     * @param createdDate: Date that the to-do entry was created.
     */
    Entry(Integer itemID, String content, String createdDate){
        this.itemID = itemID;
        this.content = content;
        this.createdDate = createdDate;
    }

    /**
     * Inserts the entry into the todo_list table, via an established SQLite database connection.
     * @param conn: Connection established to the SQLite database via JDBC.
     */
    public void insert(Connection conn) {
        // SQL query string to insert an entry into the todo_list database.
        String sql = "INSERT INTO todo_list(item_id,content,priority,category,created_date) VALUES(?,?,?,?,?)";

        // Try executing SQL query, and catch any SQLException thrown.
        try (
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Filling the PreparedStatement with the entry fields.
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

    /**
     * Sets the priority of a given to-do item.
     * @param priority: Priority setting from user input.
     */
    public void changePriority(String priority){
        this.priority = priority;
    }

    /**
     * Sets the category of a given to-do item.
     * @param category: Category setting from user input.
     */
    public void changeCategory(String category){
        this.category = category;
    }

}
