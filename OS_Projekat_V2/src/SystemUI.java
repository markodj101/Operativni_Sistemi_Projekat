

import Assembler.Assembler;
import Commands.CommandExecutor;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;

import java.util.Scanner;

public class SystemUI {
    private CommandExecutor commandExecutor;

    public SystemUI() {
        FileSystem fileSystem = new FileSystem();
        MemoryManager memoryManager = new MemoryManager(4024); // Example memory size
        DiskScheduler diskScheduler = new DiskScheduler(200); // Example disk size
        ProcessScheduler processScheduler = new ProcessScheduler(memoryManager);
        Assembler assembler = new Assembler(processScheduler, fileSystem);
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
