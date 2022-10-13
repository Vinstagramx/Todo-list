import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class which is instantiated when main.java is run.
 * Handles all terminal (interpreter) operations.
 */
public class TerminalScanner {
    /**
     * Main method to run the program.
     * Can list, filter, sort, add, and delete to-do entries in the 'todo.db' database
     * based on the user input.
     */
    public static void runToDo() {
        // New scanner for user input
        Scanner commandObj = new Scanner(System.in);
        System.out.println("Welcome to the to-do list command line tool. " +
                "Type \"help\" to see a list of available commands.");

        // Creating an instance of the Database class to establish a connection
        // to the SQLite database via JDBC
        Database testDatabase = new Database();
        Connection conn = testDatabase.connect();

        // String formatter for left-justified text in terminal
        String formatHelpString = "%-30s%s%n";

        // While boolean is true, program will continue to run,
        // unless set to false via the user command 'exit'
        Boolean run = true;

        // Initialising the array for user command arguments
        String[] commandArgs = null;
        while(run) {
            // Parsing user input and splitting command arguments by whitespaces
            String userInput = commandObj.nextLine();
            commandArgs = userInput.split("\\s+");

            // Change first command argument to lowercase for switch statement
            commandArgs[0] = commandArgs[0].toLowerCase();

            // Check first command argument
            switch (commandArgs[0]) {

                case "todo":

                    // Check second command argument
                    commandArgs[1] = commandArgs[1].toLowerCase();

                    switch (commandArgs[1]) {

                        // If user wants to create a new to-do item
                        case "new":
                            // Create DateTime string and item ID for database entry
                            LocalDate testDate = LocalDate.now();
                            Integer itemID = testDatabase.findMaxIndex() + 1;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                            // Checking for 'priority' or 'category' optional arguments
                            int[] optionalArgumentIndices = checkForOptionalArguments(commandArgs);
                            // If optional entry arguments exist, then take the smallest index as the end of the to-do text
                            int endOfTodoText = Arrays.stream(optionalArgumentIndices).filter(i -> i >= 0).min().orElse(0);

                            // Creating and filling a sub-array to concatenate the to-do text with spaces
                            // (as user input was previously split by whitespace)
                            List<String> subArray = null;
                            if(endOfTodoText > 0){
                                subArray = Arrays.asList(commandArgs).subList(2, endOfTodoText);
                            } else {
                                subArray = Arrays.asList(commandArgs).subList(2, commandArgs.length);
                            }
                            String todoMessage = String.join(" ", subArray);

                            // Creating a new Entry object and inserting the to-do item as an entry into the database
                            Entry testEntry = new Entry(itemID, todoMessage, testDate.format(formatter));

                            // If optional argument is 'priority', then add priority field
                            if (optionalArgumentIndices[0] != -1 && optionalArgumentIndices[1] == -1){

                                // The actual priority setting comes after the 'priority' keyword - i.e. 'priority high'
                                // - checks for the index of the priority setting and formats it (capitalises first letter)
                                int priorityIndex = optionalArgumentIndices[0] + 1;
                                String prioritySetting = formatPriority(commandArgs[priorityIndex]);

                                // Checks if the priority setting is valid, if not, returns an error message
                                if (!checkPriority(prioritySetting)){
                                    System.out.println("Your priority keyword is invalid. " +
                                            "Your item will be saved with \"Normal\" priority. Next time, please use \"High\",\"Normal\" or \"Low\"");
                                }
                                // If setting is valid, inserts it into the entry
                                else{
                                    testEntry.changePriority(prioritySetting);
                                }

                            // If optional argument is 'category', then add category field
                            } else if (optionalArgumentIndices[1] != -1  && optionalArgumentIndices[0] == -1){
                                // The actual category setting comes after the 'category' keyword - i.e. 'category Food'
                                // - checks for the index of the category setting
                                int categoryIndex = optionalArgumentIndices[1] + 1;

                                // Category setting may be more than one word - creates a sub-array and concatenates the words by spaces
                                List<String> categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, commandArgs.length);
                                String category = String.join(" ", categorySubArray);

                                // Enters category setting into the entry.
                                testEntry.changeCategory(category);

                            // If both optional arguments exist, then add both priority and category fields
                            } else if (optionalArgumentIndices[0] != -1 && optionalArgumentIndices[1] != -1){

                                // Obtaining, formatting and checking of the priority setting
                                int priorityIndex = optionalArgumentIndices[0] + 1;
                                String prioritySetting = formatPriority(commandArgs[priorityIndex]);
                                if (!checkPriority(prioritySetting)){
                                    System.out.println("Your priority keyword is invalid. " +
                                            "Your item will be saved with \"Normal\" priority. Next time, please use \"High\",\"Normal\" or \"Low\"");
                                }
                                // Entering the priority setting
                                else{
                                    testEntry.changePriority(prioritySetting);
                                }

                                // Obtaining and entering the category setting
                                int categoryIndex = optionalArgumentIndices[1] + 1;
                                List<String> categorySubArray = null;

                                // If category setting entered before priority setting, will concatenate category text
                                // up until the 'priority' command
                                if (optionalArgumentIndices[1] > optionalArgumentIndices[0]) {
                                    categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, commandArgs.length);
                                }
                                // If category setting entered after priority setting, will concatenate category text up
                                // until the end of the command argument array
                                else {
                                    categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, optionalArgumentIndices[0]);
                                }
                                String category = String.join(" ", categorySubArray);
                                testEntry.changeCategory(category);

                            } else{   }

                            // Printing out system text and inserting the entry into the database
                            // via the previously created connection
                            System.out.println("Creating new to-do item...");
                            testEntry.insert(conn);
                            System.out.printf("To-do item %d created.%n", itemID);
                            break;

                        // If user wants to delete a given to-do item
                        case "delete":

                            // Initialising an integer to hold the ID to delete
                            Integer idToDelete = null;

                            // Check if there is an item ID entered (corresponding to the item for deletion)
                            try {
                                // Set idToDelete according to the user input
                                idToDelete = Integer.parseInt(commandArgs[2]);
                            }
                            // If ID entered isn't of numerical format, the exception is then caught.
                            catch (NumberFormatException ex){
                                System.out.println("Invalid ID type entered. Please enter a valid numerical ID.");
                                break;
                            }
                            // Calls delete() method of the Database class, returns an error flag if the item
                            // corresponding to the entered ID doesn't exist.
                            Boolean error = testDatabase.delete(idToDelete);
                            // Print system text if error flag is not returned.
                            if(!error) {
                                System.out.println("Deleting entry...");
                                System.out.printf("To-do item %d deleted.%n", (int) idToDelete);
                            }
                            break;

                        // If an invalid command is entered after 'todo' by the user.
                        default:
                            System.out.println("Invalid command entered. Please use one of the following commands:");
                            System.out.println();
                            System.out.printf(formatHelpString, "todo new:", "Create new to-do list item.");
                            System.out.printf(formatHelpString, "todo delete:", "Delete specified to-do list item.");
                            System.out.printf(formatHelpString, "todo clear:", "Clear all to-do list items.");
                    }
                    break;

                // Help text listing the commands available to the user (if the 'help' command is used).
                case "help":
                    System.out.println("Commands available:");
                    System.out.println();
                    System.out.printf(formatHelpString, "ls:", "Display full to-do list.");
                    System.out.println("^--- sub-commands ---^");
                    System.out.printf(formatHelpString, "ls filter (category/priority):", "List and filter by category or priority.");
                    System.out.printf(formatHelpString, "ls sort (none/category/priority):", "List and sort by alphabetical order (no input) category or priority");
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
                    System.out.println("^--- optional commands ---^");
                    System.out.printf(formatHelpString, "todo new priority x:", "Create new to-do list item of priority x (\"High\", \"Normal\" or \"Low\").");
                    System.out.printf(formatHelpString, "todo new category x:", "Create new to-do list item of category x.");
                    break;

                // If no command is entered, user is prompted to enter a command.
                case "":
                    System.out.println("Please enter a command!");
                    break;

                // If 'exit' command is entered, the 'run' boolean is set to false, exiting the program.
                case "exit":
                    run = false;
                    break;

                // Same operation as 'todo delete (item)'.
                // User is able to type 'done (item number)' to remove an item from the todo list.
                case "done":
                    if (commandArgs.length == 2){
                        System.out.println("Please enter the ID of the item to complete!");
                    }
                    Integer idToDelete = null;
                    try {
                        idToDelete = Integer.parseInt(commandArgs[1]);
                    }catch (NumberFormatException ex){
                        System.out.println("Invalid ID type entered. Please enter a valid numerical ID.");
                        break;
                    }
                    Boolean error = testDatabase.delete(idToDelete);
                    if(!error) {
                        System.out.printf("Completed entry %d. %n", idToDelete);
                        System.out.printf("To-do item %d removed from list.%n", idToDelete);
                    }
                    break;

                // Calls the clear() method in the Database class if the user wants to clear all entries.
                case "clear":
                    testDatabase.clear();
                    System.out.println("Table Cleared.");
                    break;

                // Base command for listing all entries of the todo list.
                // Sub-commands 'filter' and 'sort' allow filtering and sorting of the query results
                case "ls":

                    // If the 'ls' command is used on its own, then the selectAll() method
                    // in the Database class is ran to list all entries.
                    if (commandArgs.length == 1) {
                        testDatabase.selectAll();
                    }
                    else {
                        // Checks second command keyword (after converting to lowercase)
                        commandArgs[1] = commandArgs[1].toLowerCase();
                        switch (commandArgs[1]) {

                            // Filtering of results from database query,
                            // according to priority or category specified by the user.
                            case "filter":
                                // If no priority/category is selected, or if the user enters only 'ls filter'.
                                if (commandArgs.length == 2 || commandArgs.length == 3){
                                    System.out.println("Please filter by priority or category, and " +
                                            "enter a priority or category to filter by. (e.g. ls filter priority high)");
                                }
                                // Filter by priority specified (by calling the filterPriority(priority) method in the Database class)
                                else if(commandArgs[2].toLowerCase().equals("priority")){
                                    testDatabase.filterPriority(commandArgs[3]);
                                }
                                // Filter by category specified (by calling the filterCategory(category) method in the Database class)
                                else if (commandArgs[2].toLowerCase().equals("category")){
                                    testDatabase.filterCategory(commandArgs[3]);
                                }
                                // Invalid command entered
                                else{
                                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[2]);
                                }
                                break;

                            // Sorting of results from database query,
                            // by alphabetical order (of entry contents or by category), or by priority.
                            case "sort":
                                // If only 'ls sort' entered, sorts the entries by alphabetical order of the contents.
                                if (commandArgs.length == 2){
                                    testDatabase.sort();
                                }
                                // Sorting by category - calls sortCategory() from Database class.
                                else if (commandArgs[2].toLowerCase().equals("category")){
                                    testDatabase.sortCategory();
                                }
                                // Sorting by priority - calls sortPriority() from Database class.
                                else if (commandArgs[2].toLowerCase().equals("priority")) {
                                    testDatabase.sortPriority();
                                }
                                // Invalid command keyword entered (following 'ls sort')
                                else{
                                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[2]);
                                }
                                break;

                            // Invalid second command keyword entered (following 'ls')
                            default:
                                System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[1]);
                        }
                    }
                    break;

                // Invalid first command keyword entered
                default:
                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[0]);
            }
        }
    }

    /**
     * Method to create left-justified formatting string
     * based on number of text items to display in the same line
     *
     * @return lJust: formatting string
     */
    public static String formatStringGenerator(int thingsToDisplay){
        // Initial left-justified formatting for 2 columns
        String lJust = "%6s%50s";
        // Concatenates the separate formatting strings
        // Allows 25 characters for each following field
        for (int i =2; i<thingsToDisplay;i++){
            lJust = lJust.concat("%25s");
        }
        // Adds new line to end of formatting string
        lJust = lJust.concat("%n");
        return lJust;
    }

    /**
     * Checks the command arguments array for the optional arguments 'priority' and 'category'.
     * @param commandArgs: Array of command arguments based on user input into program.
     * @return int[]{2}: integer array containing the priority and category keyword indexes
     */
    private static int[] checkForOptionalArguments(String[] commandArgs){
        // Creates a copy of the command arguments, converts all elements to lowercase via a stream.
        List<String> commandsListCopy = Arrays.asList(commandArgs).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        // Finds the indexes where the elements match 'priority' or 'category'
        // If a match is not found, returns -1 for that particular index.
        int priorityIndex = commandsListCopy.indexOf("priority");
        int categoryIndex = commandsListCopy.indexOf("category");
        return new int[]{priorityIndex, categoryIndex};
    }

    /**
     * Formats the priority setting to have a capitalised first letter, and lowercase otherwise.
     * - e.g. HIGH --> High, loW --> Low
     * @param priority: Priority setting keyword provided by user input.
     * @return formatted: Formatted string containing the priority setting.
     */
    private static String formatPriority(String priority){
        priority = priority.toLowerCase();
        // Splits original string into two substrings, capitalising the first letter,
        // and concatenating with the rest of the original string.
        String formatted = priority.substring(0, 1).toUpperCase() + priority.substring(1);
        return formatted;
    }

    /**
     * Checks if the priority setting keyword contains one of the allowed options
     * - i.e. 'High', 'Normal' or 'Low'.
     * @param priority: Priority setting keyword provided by user input.
     * @return boolean: true if priority setting matches one of the permitted options, false otherwise.
     */
    private static boolean checkPriority(String priority){
        List<String> commandsListCopy = Arrays.asList("High", "Normal", "Low");
        return commandsListCopy.contains(priority);
    }
}

// TODO: shift things up
