import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * Database class which handles SQL queries and actions (e.g. insert/delete)
 * Uses both Statements (pre-defined) and PreparedStatements (in which parameters can be inserted)
 * to interface with the todo.db database (via JDBC).
 * Operates specifically on the todo_list table within the database.
 */
public class Database {
    /**
     * Connects to the todo.db SQLite database.
     *
     * @return conn: The Connection object to the database
     */
    public Connection connect() {
        // SQLite database connection URL
        String url = "jdbc:sqlite:C:/code/training/intro-to-java/Training-todo/src/main/sqlite/db/todo.db";
        // Initialising the Connection object with a null value.
        Connection conn = null;
        // Try obtaining a connection to the database (via the URL) unless an exception is encountered.
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            // Catches the SQLException
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Deletes a specified entry from the database table.
     * @param id: Item ID of the entry to delete.
     * @return errorFlag: raised if ID is invalid.
     */
    public Boolean delete(int id) {

        // Error flag initialised as false
        Boolean errorFlag = false;
        // We firstly want to check if the entry to delete actually exists
        // SQL query string - select entries with matching item ID
        String testExists = "SELECT * FROM todo_list WHERE item_id = ?";

        // Try to execute query
        try (Connection connTest = this.connect();
             PreparedStatement pstmtTest = connTest.prepareStatement(testExists)){

            // Fill PreparedStatement with the item ID parameter, then execute
            pstmtTest.setInt(1, id);
            ResultSet rs = pstmtTest.executeQuery();

            // If query returns nothing (i.e. result set is empty), error message displayed.
            if(!rs.isBeforeFirst()){
                System.out.println("Item ID not found. Please try again with a valid Item ID."); //data does not exist
                errorFlag = true;
            }

        // Catch any SQLExceptions
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // If the entry with a matching ID does exist, then we can delete the item
        // SQL query string to delete item.
        String sql = "DELETE FROM todo_list WHERE item_id = ?";

        // Try executing the deletion and catch any exceptions
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the item ID for the deletion action
            pstmt.setInt(1, id);
            // Execute the delete statement (i.e. a table update)
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return errorFlag;
    }

    /**
     * Select and print all entries from the todo_list table
     * - i.e. print all of the todo entries.
     */
    public void selectAll(){
        // SQL query string to select all data from table
        String sql = "SELECT item_id, content, priority, category, created_date FROM todo_list";    // could do SELECT * here
        // Generate format string to be used in printf() later, so it prints nicely in terminal.
        String columnPrintFormat = TerminalScanner.formatStringGenerator(5);

        // Try executing query and catch any errors
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            // Print column headings in program
             System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                     "Category", "Date Created");
            // Loop through the result set of the SQL query
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("priority"),
                        rs.getString("category"),
                        rs.getString("created_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method to find the maximum item ID (index) of all the database entries.
     * @return maxIndex: The maximum item ID (or index) of the database entries.
     */
    public Integer findMaxIndex(){
        // SQL query string to obtain a maximum ID value
        String sql = "SELECT max(item_id) FROM todo_list";
        Integer maxIndex = null;

        // Try executing query and catch any errors.
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // Returns an integer value from the ResultSet object
            if (rs.next()) {
                maxIndex = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return maxIndex;
    }

    /**
     * Method to clear todo_list table in database.
     */
    public void clear() {
        // SQL query to remove all entries from the todo_list table.
        String sql = "DELETE FROM todo_list";   // Note: can also do "DELETE FROM todo_list WHERE 1=1";

        // Try executing the table update, and catch any SQLException that gets thrown.
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement()){
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the todo_list table, and filters by a specified priority type entered by the user
     * - i.e. 'Low'/'Normal'/'High'
     * @param keyword: Priority keyword from user input.
     */
    public void filterPriority(String keyword){
        // Creates a list of allowed priorities to filter by.
        List<String> permittedPriorities = Arrays.asList("High", "Normal", "Low");
        // Formats the entered keyword to have a capitalised first letter, and lowercase otherwise.
        keyword = keyword.toLowerCase();
        keyword = keyword.substring(0, 1).toUpperCase() + keyword.substring(1);

        // If priority setting is valid, then carry out the SQL query and filtering
        if (permittedPriorities.contains(keyword)){
            // SQL query string where priority is yet to be defined
            String sql = "SELECT item_id, content, priority, category, created_date FROM todo_list WHERE priority = ?";
            // Generate format string to be used in printf() later, so it prints nicely in terminal.
            String columnPrintFormat = TerminalScanner.formatStringGenerator(5);

            // Try to execute query, and catch SQLException if thrown
            try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Set the corresponding priority parameter in the PreparedStatement
                pstmt.setString(1, keyword);
                ResultSet rs = pstmt.executeQuery();
                // Print column headings
                System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                        "Category", "Date Created");
                // Loop through the ResultSet object from carrying out query and print data entries
                while (rs.next()) {
                    System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                            rs.getString("content"),
                            rs.getString("priority"),
                            rs.getString("category"),
                            rs.getString("created_date"));

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        // If priority setting is invalid, error message displayed.
        else {
            System.out.printf("Cannot filter by priority \"%s\". Please enter a valid priority keyword!%n", keyword);
        }
    }

    /**
     * Prints to-do items, filtered by a category specified by user input.
     * @param keyword: Category keyword from user input.
     */
    public void filterCategory(String keyword){
        // SQL query string to filter by a category
        String sql = "SELECT item_id, content, priority, category, created_date FROM todo_list WHERE category = ?";
        // Generate format string to be used in printf() later, so it prints nicely in terminal.
        String columnPrintFormat = TerminalScanner.formatStringGenerator(5);

        // Try to execute query, and catch SQLException if thrown
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the corresponding category parameter in the PreparedStatement
            pstmt.setString(1, keyword);
            ResultSet rs = pstmt.executeQuery();
            // Print column headings
            System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                    "Category", "Date Created");

            // Try to execute query, and catch SQLException if thrown
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("priority"),
                        rs.getString("category"),
                        rs.getString("created_date"));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the todo_list table, and prints all entries,
     * sorted in alphabetical order by the to-do list contents
     */
    public void sort(){
        // SQL query string to sort by alphabetical order (non case-sensitive)
        String sql = "SELECT * FROM todo_list ORDER BY LOWER(content)";
        String columnPrintFormat = TerminalScanner.formatStringGenerator(5);

        // Try to execute query, and catch SQLException if thrown
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            // Print column headings
            System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                    "Category", "Date Created");
            // Loop through the ResultSet object from carrying out query and print data entries
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("priority"),
                        rs.getString("category"),
                        rs.getString("created_date"));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the todo_list table, and prints all entries sorted by priority
     * - i.e. shown in order of 'High' --> 'Normal' --> 'Low'
     */
    public void sortPriority(){
        // SQL query string to sort by priority
        // Custom order needed to be set via a CASE, since priority levels aren't in alphabetical order
        String sql = "SELECT * FROM todo_list ORDER BY CASE priority WHEN 'High' THEN 1 WHEN 'Normal' THEN 2 WHEN 'Low' THEN 3 ELSE 4 END";

        String columnPrintFormat = TerminalScanner.formatStringGenerator(5);
        // Try to execute query, and catch SQLException if thrown
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            // Print column headings
            System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                    "Category", "Date Created");
            // Loop through the ResultSet object from carrying out query and print data entries
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("priority"),
                        rs.getString("category"),
                        rs.getString("created_date"));
            }
//
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the todo_list table, and prints all entries sorted by category
     * - in alphabetical, non case-sensitive order
     */
    public void sortCategory(){
        // SQL query string to sort categories by alphabetical order (non case-sensitive)
        // Note that to-do items without a category are printed last.
        String sql = "SELECT * FROM todo_list ORDER BY CASE LOWER(category) WHEN '' THEN 2 ELSE 1 END";
        String columnPrintFormat = TerminalScanner.formatStringGenerator(5);

        // Try to execute query, and catch SQLException if thrown
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement()){

            ResultSet rs = stmt.executeQuery(sql);
            // Print column headings
            System.out.printf(columnPrintFormat, "Item ID", "To-Do Content", "Priority",
                    "Category", "Date Created");

            // Loop through the ResultSet object from carrying out query and print data entries
            while (rs.next()) {
                System.out.printf(columnPrintFormat, rs.getInt("item_id"),
                        rs.getString("content"),
                        rs.getString("priority"),
                        rs.getString("category"),
                        rs.getString("created_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
