package Assembler;

import Rasporedjivanje.ProcessScheduler;
import FileSistem.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class Assembler {
    private ProcessScheduler processScheduler;
    private FileSystem fileSystem;

    public Assembler() {
    }

    public void setProcessScheduler(ProcessScheduler processScheduler, FileSystem fileSystem) {
        this.processScheduler = processScheduler;
        this.fileSystem = fileSystem;
    }

    public void execute(String[] commandParts) {
        if (commandParts.length != 2) {
            System.out.println("Nevazeca RUN komanda. Uslov koriscenja: run <fajl>");
            return;
        }
        String fileName = commandParts[1];

        // Provera ekstenzije
        if (!fileName.endsWith(".asm")) {
            System.out.println("Fajl " + fileName + " nema .asm ekstenziju i ne može biti pokrenut.");
            return;
        }

        String currentDirPath = fileSystem.getCurrentDirPath();
        String absolutePath = Paths.get(currentDirPath, fileName).toString();
        System.out.println("Izvrsavanje fajla: " + absolutePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(absolutePath)));
            processScheduler.addProcess(fileName, content);
        } catch (IOException e) {
            System.out.println("Neuspjesno citanje fajla " + absolutePath);
        }
    }

    public void runInstructions(String instructions) throws InterruptedException {
        Stack<Integer> stack = new Stack<>();
        String[] lines = instructions.split("\n");

        for (String line : lines) {
            String[] parts = line.trim().split(" ");
            switch (parts[0]) {
                case "PUSH":
                    stack.push(Integer.parseInt(parts[1]));
                    break;
                case "POP":
                    if (!stack.isEmpty()) {
                        System.out.println("POP: " + stack.pop());
                    } else {
                        System.out.println("Stack je prazan");
                    }
                    break;
                case "ADD":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b + a);
                    } else {
                        System.out.println("Nedovoljno vrijednosti u stack-u");
                    }
                    break;
                case "SUB":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b - a);
                    } else {
                        System.out.println("Nedovoljno vrijednosti u stack-u");
                    }
                    break;
                case "MUL":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b * a);
                    } else {
                        System.out.println("Nedovoljno vrijednosti u stack-u");
                    }
                    break;
                case "DIV":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b / a);
                    } else {
                        System.out.println("Nedovoljno vrijednosti u stack-u");
                    }
                    break;
                default:
                    System.out.println("Nepoznata instrukcija: " + parts[0]);
                    break;
            }
            // Dodaj delay za svaku instrukciju da simulira vreme izvršavanja
            Thread.sleep(500); // 500 ms delay za svaku instrukciju
        }

        System.out.println("Finalni stack: " + stack);
    }
}
