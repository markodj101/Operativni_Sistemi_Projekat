package Commands;

import Assembler.Assembler;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;

public class CommandExecutor {
    private final FileSystem fileSystem;
    private final ProcessScheduler processScheduler;
    private final MemoryManager memoryManager;
    private final DiskScheduler diskScheduler;
    private final Assembler assembler;

    public CommandExecutor(FileSystem fileSystem, ProcessScheduler processScheduler, MemoryManager memoryManager, DiskScheduler diskScheduler, Assembler assembler) {
        this.fileSystem = fileSystem;
        this.processScheduler = processScheduler;
        this.memoryManager = memoryManager;
        this.diskScheduler = diskScheduler;
        this.assembler = assembler;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0].toLowerCase()) {
            case "cd":
                if (parts.length != 2) {
                    System.out.println("Nevažeća CD komanda.");
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
            case "..":
                fileSystem.handleBackCommand();
                break;
            case "mkdir":
                if (parts.length != 2) {
                    System.out.println("Nevažeća MKDIR komanda.");
                    break;
                }
                fileSystem.createDirectory(parts[1]);
                break;
            case "run":
                assembler.execute(parts);
                break;
            case "mem":
                System.out.println("Korišćena memorija: " + processScheduler.getMemoryUsage() + " bytes");
                System.out.println("Slobodna memorija: " + memoryManager.getFreeMemory() + " bytes");
                break;
            case "exit":
                System.out.println("Gašenje...");
                System.exit(0);
                break;
            case "rm":
                if (parts.length != 2) {
                    System.out.println("Nevažeća RM komanda.");
                    break;
                }
                fileSystem.delete(parts[1]);
                break;
            case "block":
                if (parts.length != 2) {
                    System.out.println("Nevažeća BLOCK komanda.");
                    break;
                }
                processScheduler.blockProcess(parts[1]);
                break;
            case "unblock":
                if (parts.length != 2) {
                    System.out.println("Nevažeća UNBLOCK komanda.");
                    break;
                }
                processScheduler.unblockProcess(parts[1]);
                break;
            case "terminate":
                if (parts.length != 2) {
                    System.out.println("Nevažeća TERMINATE komanda.");
                    break;
                }
                processScheduler.terminateProcess(parts[1]);
                break;
            case "diskreq":
                if (parts.length != 2) {
                    System.out.println("Nevažeća DISKREQ komanda.");
                    break;
                }
                try {
                    int position = Integer.parseInt(parts[1]);
                    diskScheduler.addRequest(position);
                } catch (NumberFormatException e) {
                    System.out.println("Nevažeća pozicija: " + parts[1]);
                }
                break;
            case "diskexecute":
                diskScheduler.executeRequests();
                break;
            case "showblocks":
                if (parts.length != 2) {
                    System.out.println("Nevažeća SHOWBLOCKS komanda.");
                    break;
                }
                processScheduler.showProcessBlocks(parts[1]);
                break;
            case "showfreeblocks":
                fileSystem.showFreeBlocks();
                break;
            case "tree":
                fileSystem.printDirectoryTree();
                break;
            case "help":
                helpCommands();
                break;
            default:
                System.out.println("Nepoznata komanda: " + parts[0]);
                break;
        }
    }

    private void helpCommands() {
        System.out.println("Dostupne komande:");
        System.out.println("CD <direktorijum> - Promena direktorijuma");
        System.out.println("DIR ili LS - Lista trenutnog direktorijuma");
        System.out.println("TREE - Prikaz hijerarhijske strukture direktorijuma");
        System.out.println("PS - Lista pokrenutih procesa");
        System.out.println("MKDIR <direktorijum> - Kreiranje direktorijuma");
        System.out.println("RUN <fajl> - Pokretanje procesa");
        System.out.println("MEM - Status korišćenosti memorije");
        System.out.println("EXIT - Gašenje sistema");
        System.out.println("RM <fajl/direktorijum> - Uklanjanje fajla/direktorijuma");
        System.out.println("BLOCK <proces> - Blokiranje procesa");
        System.out.println("UNBLOCK <proces> - Odblokiranje procesa");
        System.out.println("TERMINATE <proces> - Terminacija procesa");
        System.out.println("DISKREQ <pozicija> - Dodaj disk zahtev na poziciju");
        System.out.println("DISKEXECUTE - Izvršavanje svih zahteva po C-SCAN tehnici");
        System.out.println("SHOWBLOCKS <fajl> - Prikaz blokova fajla");
        System.out.println("SHOWFREEBLOCKS - Prikaz slobodnih blokova");
        System.out.println(".. - Povratak na prethodni direktorijum");
    }
}
