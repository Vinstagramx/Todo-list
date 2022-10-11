import java.sql.Connection;

public class main {

    public static void main(String[] args) {
        Database testDatabase = new Database();
        Connection conn = testDatabase.connect();
        TerminalScanner.runToDo(conn);
    }
}
