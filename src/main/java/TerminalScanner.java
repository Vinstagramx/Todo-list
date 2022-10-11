import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TerminalScanner {

    public static void runToDo() {
        Scanner commandObj = new Scanner(System.in);
        System.out.println("Welcome to the to-do list command line tool.");
        Database testDatabase = new Database();
        Connection conn = testDatabase.connect();
//      String formatter for left-justified text in terminal
//        String formatHelpString = "%-20s%s%n";
//        String format2 = "%-20s%s%20s%n";
        Boolean run = true;
        String[] commandArgs = null;
        while(run) {
            //      Parsing user input and splitting command arguments by spaces
            String userInput = commandObj.nextLine();
            commandArgs = userInput.split("\\s+");
            commandArgs[0] = commandArgs[0].toLowerCase();
            switch (commandArgs[0]) {
                case "todo":
                    commandArgs[1] = commandArgs[1].toLowerCase();
                    switch (commandArgs[1]) {
                        case "new":
                            System.out.println("Creating new to-do item...");

                            LocalDate testDate = LocalDate.now();
                            Integer itemID = testDatabase.findMaxIndex() + 1;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                            List<String> subArray = Arrays.asList(commandArgs).subList(2, commandArgs.length);
                            String todoMessage = String.join(" ", subArray);

                            Entry testEntry = new Entry(itemID, todoMessage, testDate.format(formatter));
                            testEntry.insert(conn);
                            System.out.printf("To-do item %d created.%n", itemID);
                            break;
                        case "delete":
                            System.out.println("Deleting entry...");
                            Integer idToDelete = Integer.parseInt(commandArgs[2]);
//                            Entry testEntry2 = new Entry();
                            testDatabase.delete(idToDelete);
                            System.out.printf("To-do item %d deleted.%n", (int)idToDelete);
                            break;
                        default:
                            String formatInvalidString = formatStringGenerator(2);
                            System.out.println("Invalid command entered. Please use one of the following commands:");
                            System.out.println();
                            System.out.printf(formatInvalidString, "todo new:", "Create new to-do list item.");
                            System.out.printf(formatInvalidString, "todo delete:", "Delete specified to-do list item.");
                            System.out.printf(formatInvalidString, "todo clear:", "Clear all to-do list items.");
                    }
                    break;
                case "help":
                    String formatHelpString = formatStringGenerator(2);
                    System.out.println("Commands available:");
                    System.out.println();
                    System.out.printf(formatHelpString, "ls:", "Display full to-do list.");
                    System.out.println();
                    System.out.printf(formatHelpString, "done + number:", "Mark to-do item (number) as done.");
                    System.out.println();
                    System.out.printf(formatHelpString, "clear:", "Clear all to-do list items.");
                    System.out.println();
                    System.out.printf(formatHelpString, "exit:", "Exit to-do list program.");
                    System.out.println();
                    System.out.printf(formatHelpString, "todo:", "Access and edit to-do list items.");
                    System.out.println("^--- sub-commands ---^");
                    System.out.printf(formatHelpString, "todo new:", "Create new to-do list item.");
                    System.out.printf(formatHelpString, "todo delete:", "Delete specified to-do list item.");
                    break;
                case "":
                    System.out.println("Please enter a to-do item!");
                    break;
                case "exit":
                    run = false;
                    break;
                case "done":
                    Integer idToDelete = Integer.parseInt(commandArgs[1]);
                    System.out.printf("Completed entry %d. %n", idToDelete);
//                    Entry testEntry2 = new Entry();
                    testDatabase.delete(idToDelete);
                    System.out.printf("To-do item %d removed from list.%n", idToDelete);
                    break;
                case "ls":
                    testDatabase.selectAll();
                    break;
                default:
                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[0]);
            }
        }
    }
//        Use SQLite database/postgres
//        Java lists
//        if(commandArgs.length > 2){
//            throw new RuntimeException("More than two arguments. Please try again!");
//        }

    /**
    Method to create left-justified formatting string
     based on number of text items to display in the same line
     */
    public static String formatStringGenerator(int thingsToDisplay){
//        String lJust = "%-20s%s";
        String lJust = "%6s%40s";
        for (int i =2; i<thingsToDisplay;i++){
            lJust = lJust.concat("%40s");
        }
        lJust = lJust.concat("%n");
        return lJust;
    }
}

// TODO: filter with streams :))))))))))
// TODO: shift things up
// TODO: associate ids, database is its own class, entry is its own class which is created
