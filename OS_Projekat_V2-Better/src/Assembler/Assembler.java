package Assembler;

import Rasporedjivanje.ProcessScheduler;
import FileSistem.FileSystem;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class Assembler {
    private ProcessScheduler processScheduler;
    private FileSystem fileSystem;
    private TextArea outputArea;

    public Assembler(TextArea outputArea) {
        this.outputArea = outputArea;
    }

    public void setProcessScheduler(ProcessScheduler processScheduler, FileSystem fileSystem) {
        this.processScheduler = processScheduler;
        this.fileSystem = fileSystem;
    }

    public String execute(String[] commandParts) {
        if (commandParts.length != 2) {
            return "Nevazeca RUN komanda. Uslov koriscenja: run <fajl>";
        }
        String fileName = commandParts[1];

        // Provjera ekstenzije
        if (!fileName.endsWith(".asm")) {
            return "Fajl " + fileName + " nema .asm ekstenziju i ne može biti pokrenut.";
        }

        String currentDirPath = fileSystem.getCurrentDirPath();
        String absolutePath = Paths.get(currentDirPath, fileName).toString();
        StringBuilder result = new StringBuilder("Izvrsavanje fajla: " + absolutePath + "\n");

        try {
            String content = new String(Files.readAllBytes(Paths.get(absolutePath)));
            processScheduler.addProcess(fileName, content);
        } catch (IOException e) {
            return "Neuspjesno citanje fajla " + absolutePath;
        }

        return result.toString();
    }

    public void runInstructions(String instructions) throws InterruptedException {
        Stack<Integer> stack = new Stack<>();
        String[] lines = instructions.split("\n");

        for (String line : lines) {
            String[] parts = line.trim().split(" ");
            switch (parts[0]) {
                case "PUSH":
                    stack.push(Integer.parseInt(parts[1]));
                    outputToGui("PUSH: " + parts[1] + "\n");
                    break;
                case "POP":
                    if (!stack.isEmpty()) {
                        outputToGui("POP: " + stack.pop() + "\n");
                    } else {
                        outputToGui("Stack je prazan\n");
                    }
                    break;
                case "ADD":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b + a);
                        outputToGui("ADD: " + b + " + " + a + " = " + (b + a) + "\n");
                    } else {
                        outputToGui("Nedovoljno vrijednosti u stack-u\n");
                    }
                    break;
                case "SUB":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b - a);
                        outputToGui("SUB: " + b + " - " + a + " = " + (b - a) + "\n");
                    } else {
                        outputToGui("Nedovoljno vrijednosti u stack-u\n");
                    }
                    break;
                case "MUL":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b * a);
                        outputToGui("MUL: " + b + " * " + a + " = " + (b * a) + "\n");
                    } else {
                        outputToGui("Nedovoljno vrijednosti u stack-u\n");
                    }
                    break;
                case "DIV":
                    if (stack.size() >= 2) {
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b / a);
                        outputToGui("DIV: " + b + " / " + a + " = " + (b / a) + "\n");
                    } else {
                        outputToGui("Nedovoljno vrijednosti u stack-u\n");
                    }
                    break;
                default:
                    outputToGui("Nepoznata instrukcija: " + parts[0] + "\n");
                    break;
            }
            Thread.sleep(500);
        }

        outputToGui("Finalni stack: " + stack + "\n");
    }

    private void outputToGui(String message) {
        outputArea.appendText(message);
    }
}
