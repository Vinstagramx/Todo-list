import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TerminalScanner {

    public static void runToDo() {
        Scanner commandObj = new Scanner(System.in);
        System.out.println("Welcome to the to-do list command line tool. " +
                "Type \"help\" to see a list of available commands.");
        Database testDatabase = new Database();
        Connection conn = testDatabase.connect();
        // String formatter for left-justified text in terminal
        String formatHelpString = "%-30s%s%n";
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
                            LocalDate testDate = LocalDate.now();
                            Integer itemID = testDatabase.findMaxIndex() + 1;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                            int[] optionalArgumentIndices = checkForOptionalArguments(commandArgs);
                            int endOfTodoText = Arrays.stream(optionalArgumentIndices).filter(i -> i >= 0).min().orElse(0);

                            List<String> subArray = null;
                            if(endOfTodoText > 0){
                                subArray = Arrays.asList(commandArgs).subList(2, endOfTodoText);
                            } else {
                                subArray = Arrays.asList(commandArgs).subList(2, commandArgs.length);
                            }
                            String todoMessage = String.join(" ", subArray);

                            Entry testEntry = new Entry(itemID, todoMessage, testDate.format(formatter));
                            if (optionalArgumentIndices[0] != -1 && optionalArgumentIndices[1] == -1){
                                int priorityIndex = optionalArgumentIndices[0] + 1;
                                String prioritySetting = formatPriority(commandArgs[priorityIndex]);
                                if (!checkPriority(prioritySetting)){
                                    System.out.println("Your priority keyword is invalid. " +
                                            "Your item will be saved with \"Normal\" priority. Next time, please use \"High\",\"Normal\" or \"Low\"");
                                }
                                else{
                                    testEntry.changePriority(prioritySetting);
                                }
                            } else if (optionalArgumentIndices[1] != -1  && optionalArgumentIndices[0] == -1){
                                int categoryIndex = optionalArgumentIndices[1] + 1;
                                List<String> categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, commandArgs.length);
                                String category = String.join(" ", categorySubArray);
                                testEntry.changeCategory(category);
                            } else if (optionalArgumentIndices[0] != -1 && optionalArgumentIndices[1] != -1){

                                int priorityIndex = optionalArgumentIndices[0] + 1;
                                String prioritySetting = formatPriority(commandArgs[priorityIndex]);
                                if (!checkPriority(prioritySetting)){
                                    System.out.println("Your priority keyword is invalid. " +
                                            "Your item will be saved with \"Normal\" priority. Next time, please use \"High\",\"Normal\" or \"Low\"");
                                }
                                else{
                                    testEntry.changePriority(prioritySetting);
                                }

                                int categoryIndex = optionalArgumentIndices[1] + 1;
                                List<String> categorySubArray = null;
                                if (optionalArgumentIndices[1] > optionalArgumentIndices[0]) {
                                    categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, commandArgs.length);
                                }
                                else {
                                    categorySubArray = Arrays.asList(commandArgs).subList(categoryIndex, optionalArgumentIndices[0]);
                                }
                                String category = String.join(" ", categorySubArray);
                                testEntry.changeCategory(category);

                            } else{   }

                            System.out.println("Creating new to-do item...");
                            testEntry.insert(conn);
                            System.out.printf("To-do item %d created.%n", itemID);
                            break;
                        case "delete":
                            Integer idToDelete = null;
                            try {
                                idToDelete = Integer.parseInt(commandArgs[2]);
                            }catch (NumberFormatException ex){
                                System.out.println("Invalid ID type entered. Please enter a valid numerical ID.");
                                break;
                            }
                            Boolean error = testDatabase.delete(idToDelete);
                            if(!error) {
                                System.out.println("Deleting entry...");
                                System.out.printf("To-do item %d deleted.%n", (int) idToDelete);
                            }
                            break;
                        default:
//                            String formatInvalidString = formatStringGenerator(2);
                            System.out.println("Invalid command entered. Please use one of the following commands:");
                            System.out.println();
                            System.out.printf(formatHelpString, "todo new:", "Create new to-do list item.");
                            System.out.printf(formatHelpString, "todo delete:", "Delete specified to-do list item.");
                            System.out.printf(formatHelpString, "todo clear:", "Clear all to-do list items.");
                    }
                    break;
                case "help":
                    // Help Text
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
                case "":
                    System.out.println("Please enter a to-do item!");
                    break;
                case "exit":
                    run = false;
                    break;
                case "done":
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
                case "clear":
                    testDatabase.clear();
                    System.out.println("Table Cleared.");
                    break;
                case "ls":
                    if (commandArgs.length == 1) {
                        testDatabase.selectAll();
                    }
                    else {
                        commandArgs[1] = commandArgs[1].toLowerCase();
                        switch (commandArgs[1]) {
                            case "filter":
                                if (commandArgs[2].toLowerCase().equals("priority")){
//                                    System.out.println("test2");
                                    testDatabase.filterPriority(commandArgs[3]);
                                }
                                else if (commandArgs[2].toLowerCase().equals("category")){
                                    testDatabase.filterCategory(commandArgs[3]);
                                }
                                else{
                                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[2]);
                                }
                                break;
                            case "sort":
                                if (commandArgs.length == 2){
                                    testDatabase.sort();
                                }
                                else if (commandArgs[2].toLowerCase().equals("category")){
                                    testDatabase.sortCategory();
                                }
                                else if (commandArgs[2].toLowerCase().equals("priority")) {
                                    testDatabase.sortPriority();
                                }
                                else{
                                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[2]);
                                }
                                break;
                            default:
                                System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[1]);
                        }
                    }
                    break;
                default:
                    System.out.printf("Command \"%s\" invalid. Please enter a different command!%n", commandArgs[0]);
            }
        }
    }

    /**
     * Method to create left-justified formatting string
     * based on number of text items to display in the same line
     *
     * @return lJust - Formatted String
     */
    public static String formatStringGenerator(int thingsToDisplay){
//        String lJust = "%-20s%s";
        String lJust = "%6s%50s";
        for (int i =2; i<thingsToDisplay;i++){
            lJust = lJust.concat("%25s");
        }
        lJust = lJust.concat("%n");
        return lJust;
    }

    private static int[] checkForOptionalArguments(String[] commandArgs){
        List<String> commandsListCopy = Arrays.asList(commandArgs).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        int priorityIndex = commandsListCopy.indexOf("priority");
        int categoryIndex = commandsListCopy.indexOf("category");
//        System.out.println("p " + priorityIndex + "c " + categoryIndex);
        return new int[]{priorityIndex, categoryIndex};
    }

    private static String formatPriority(String priority){
        priority = priority.toLowerCase();
        String formatted = priority.substring(0, 1).toUpperCase() + priority.substring(1);
        return formatted;
    }
    private static boolean checkPriority(String priority){
        List<String> commandsListCopy = Arrays.asList("High", "Normal", "Low");
        return commandsListCopy.contains(priority);
    }
}

// TODO: filter with streams :))))))))))
// TODO: shift things up
