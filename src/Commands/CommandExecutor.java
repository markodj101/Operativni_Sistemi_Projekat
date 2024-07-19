package Commands;

import Assembler.Assembler;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;

public class CommandExecutor {
    private FileSystem fileSystem;
    private ProcessScheduler processScheduler;
    private MemoryManager memoryManager;
    private DiskScheduler diskScheduler;
    private Assembler assembler;

    public CommandExecutor(FileSystem fileSystem, ProcessScheduler processScheduler, MemoryManager memoryManager, DiskScheduler diskScheduler, Assembler assembler) {
        this.fileSystem = fileSystem;
        this.processScheduler = processScheduler;
        this.memoryManager = memoryManager;
        this.diskScheduler = diskScheduler;
        this.assembler = assembler;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "cd":
                if (parts.length != 2) {
                    System.out.println("Invalid cd command.");
                    break;
                }
                fileSystem.changeDirectory(parts[1]);
                break;
            case "dir":
            case "ls":
                fileSystem.listCurrentDirectory();
                break;
            case "ps":
                processScheduler.showProcessList();
                break;
            case "mkdir":
                if (parts.length != 2) {
                    System.out.println("Invalid mkdir command.");
                    break;
                }
                fileSystem.createDirectory(parts[1]);
                break;
            case "run":
                assembler.execute(parts);
                break;
            case "mem":
                System.out.println("Used memory: " + processScheduler.getMemoryUsage() + " bytes");
                System.out.println("Free memory: " + memoryManager.getFreeMemory() + " bytes");
                break;
            case "exit":
                System.out.println("Shutting down...");
                System.exit(0);
                break;
            case "rm":
                if (parts.length != 2) {
                    System.out.println("Invalid rm command.");
                    break;
                }
                fileSystem.delete(parts[1]);
                break;
            case "help":
                System.out.println("Available commands:");
                System.out.println("cd <directory> - Change directory");
                System.out.println("dir or ls - List current directory");
                System.out.println("ps - List processes");
                System.out.println("mkdir <directory> - Create directory");
                System.out.println("run <file> - Run process");
                System.out.println("mem - Show memory usage");
                System.out.println("exit - Shut down system");
                System.out.println("rm <file/directory> - Remove file or directory");
                break;
            default:
                System.out.println("Unknown command: " + parts[0]);
                break;
        }
    }
}
