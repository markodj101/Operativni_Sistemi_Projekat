package Assembler;

import java.util.Scanner;

public class Assembler {
    public static void main(String[] args) {
        StackMachine machine = new StackMachine();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Unesite instrukcije za asembler. Tip 'EXIT' za izlaz.");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("EXIT")) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length > 0) {
                try {
                    instrukcije command = instrukcije.valueOf(parts[0].toUpperCase());
                    Integer operand = parts.length > 1 ? Integer.valueOf(parts[1]) : null;
                    machine.execute(String.valueOf(command), operand);
                    machine.printStack();
                } catch (IllegalArgumentException e) {
                    System.out.println("Nepoznata instrukcija: " + parts[0]);
                }
            }
        }

        scanner.close();
    }
}