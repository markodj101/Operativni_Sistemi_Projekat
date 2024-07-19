package Assembler;

import FileSistem.FileSystem;
import Rasporedjivanje.ProcessScheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Assembler {
    private ProcessScheduler processScheduler;
    private FileSystem fileSystem;

    public Assembler(ProcessScheduler processScheduler, FileSystem fileSystem) {
        this.processScheduler = processScheduler;
        this.fileSystem = fileSystem;
    }

    public void execute(String[] commandParts) {
        if (commandParts.length != 2) {
            System.out.println("Invalid run command. Usage: run <file>");
            return;
        }
        String fileName = commandParts[1];

        // Get the current directory from the file system
        String currentDirPath = fileSystem.getCurrentDir().getAbsolutePath();

        // Construct the absolute path to the file
        String absolutePath = Paths.get(currentDirPath, fileName).toString();
        System.out.println("Executing file: " + absolutePath);

        try {
            String content = new String(Files.readAllBytes(Paths.get(absolutePath)));
            int memorySize = content.length(); // Velicina fajla
            processScheduler.addProcess(fileName, content, memorySize);
        } catch (IOException e) {
            System.out.println("Failed to read file: " + absolutePath);
            System.out.println(e);
        }
    }
}
