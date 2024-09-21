import Assembler.Assembler;
import Commands.CommandExecutor;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;

import java.util.Scanner;

public class SystemUI {
    private final CommandExecutor commandExecutor;

    public SystemUI() {
        FileSystem fileSystem = new FileSystem();
        MemoryManager memoryManager = new MemoryManager(12000); // Primjer veličine memorije
        DiskScheduler diskScheduler = new DiskScheduler(200); // Primjer veličine diska
        ProcessScheduler processScheduler = new ProcessScheduler(memoryManager, fileSystem);
        Assembler assembler = new Assembler();
        processScheduler.setAssembler(assembler);
        assembler.setProcessScheduler(processScheduler, fileSystem);
        this.commandExecutor = new CommandExecutor(fileSystem, processScheduler, memoryManager, diskScheduler, assembler);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            commandExecutor.executeCommand(command);
        }
    }

    public static void main(String[] args) {
        SystemUI ui = new SystemUI();
        ui.start();
    }
}
