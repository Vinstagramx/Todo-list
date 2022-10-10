import java.util.Scanner;

public class TerminalScanner {

    public static void runToDo() {
        Scanner commandObj = new Scanner(System.in);
        System.out.println("Welcome to the to-do list command line tool.");

//      String formatter for left-justified text in terminal
        String format = "%-20s%s%n";
        Boolean run = true;
        String[] commandArgs = null;
        while(run) {
            //      Parsing user input and splitting command arguments by spaces
            String userInput = commandObj.nextLine().toLowerCase();
            commandArgs = userInput.split("\\s+");
            switch (commandArgs[0]) {
                case "todo":
                    switch (commandArgs[1]) {
                        case "new":
                            System.out.println("Creating new to-do item...");
                    }
                    break;
                case "help":
                    System.out.println("Commands available:");
                    System.out.println();
                    System.out.printf(format, "ls:", "Display full to-do list.");
                    System.out.println();
                    System.out.printf(format, "todo:", "Access and edit to-do list items.");
                    System.out.println("^ sub-commands:");
                    System.out.printf(format, "new:", "Create new to-do list item.");
                    System.out.printf(format, "delete:", "Delete specified to-do list item.");
                    System.out.printf(format, "clear:", "Clear all to-do list items.");
                    System.out.println();
                    System.out.printf(format, "exit:", "Exit to-do list program.");
                    break;
                case "exit":
                    run = false;
                    break;
                default:
                    System.out.println("Command %s invalid. Please enter a different command!".format(commandArgs[0]));
            }
        }

//        Use SQLite database/postgres
//        Java lists
        if(commandArgs.length > 2){
            throw new RuntimeException("More than two arguments. Please try again!");
        }
    }
}

// TODO: filter with streams :))))))))))
