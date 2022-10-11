import java.sql.*;

public class InitialDatabaseCreation {
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/code/training/intro-to-java/Training-todo/src/main/sqlite/db/" + fileName;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        createNewDatabase("todo.db");
    }

}
