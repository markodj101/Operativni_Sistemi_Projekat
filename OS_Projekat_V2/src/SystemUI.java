import Commands.CommandExecutor;

import java.util.Scanner;



public class SystemUI {
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        CommandExecutor executor = new CommandExecutor();

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            executor.execute(command);
        }
    }

    public static void main(String[] args) {
        start();
    }
}