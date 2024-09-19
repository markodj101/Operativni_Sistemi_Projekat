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

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        String[] parts = command.split(" ");
        Object result;
        switch (parts[0].toLowerCase()) {
            case "cd":
                if (parts.length != 2) {
                    return "Nevazeca CD komanda.";
                }
                result = fileSystem.changeDirectory(parts[1]);
                break;
            case "dir":
            case "ls":
                result = fileSystem.listCurrentDirectory();
                break;
            case "ps":
                result = processScheduler.showProcessList();
                break;
            case "..":
                result = fileSystem.handleBackCommand();
                break;
            case "mkdir":
                if (parts.length != 2) {
                    return "Nevazeca MKDIR komanda.";
                }
                result = fileSystem.createDirectory(parts[1]);
                break;
            case "run":
                result = assembler.execute(parts);
                break;
            case "mem":
                output.append("Koristena memorija: ").append(processScheduler.getMemoryUsage()).append(" bytes\n");
                output.append("Slobodna memorija: ").append(memoryManager.getFreeMemory()).append(" bytes");
                return output.toString();
            case "exit":
                return "Gasenje...";
            case "rm":
                if (parts.length != 2) {
                    return "Nevazeca RM komanda.";
                }
                result = fileSystem.delete(parts[1]);
                break;
            case "block":
                if (parts.length != 2) {
                    return "Nevazeca BLOCK komanda.";
                }
                result = processScheduler.blockProcess(parts[1]);
                break;
            case "unblock":
                if (parts.length != 2) {
                    return "Nevazeca UNBLOCK komanda.";
                }
                result = processScheduler.unblockProcess(parts[1]);
                break;
            case "terminate":
                if (parts.length != 2) {
                    return "Nevazeca TERMINATE komanda.";
                }
                result = processScheduler.terminateProcess(parts[1]);
                break;
            case "diskreq":
                if (parts.length != 2) {
                    return "Nevazeca DISKREQ komanda.";
                }
                try {
                    int position = Integer.parseInt(parts[1]);
                    result = diskScheduler.addRequest(position);
                } catch (NumberFormatException e) {
                    return "Nevazeca pozicija: " + parts[1];
                }
                break;
            case "diskexecute":
                result = diskScheduler.executeRequests();
                break;
            case "showblocks":
                if (parts.length != 2) {
                    return "Nevazeca SHOWBLOCKS komanda.";
                }
                result = processScheduler.showProcessBlocks(parts[1]);
                break;
            case "showfreeblocks":
                result = fileSystem.showFreeBlocks();
                break;
            case "tree":
                result = fileSystem.printDirectoryTree();
                break;
            case "help":
                return HelpCommands();
            default:
                return "Nepoznata komanda: " + parts[0];
        }
        return result != null ? result.toString() : "Komanda izvršena bez rezultata.";
    }

    private String HelpCommands() {
        StringBuilder help = new StringBuilder("Dostupne komande:\n");
        help.append("CD <direktorijum> - Promjena direktorijuma\n");
        help.append("DIR ili LS - Lista trenutnog direktorijuma\n");
        help.append("TREE - Prikaz hijerarhijske strukture direktorijuma\n");
        help.append("PS - Lista pokrenutih procesa\n");
        help.append("MKDIR <direktorijum> - Kreiranje direktorijuma\n");
        help.append("RUN <fajl> - Pokretanje procesa\n");
        help.append("MEM - Status koristenosti memorije\n");
        help.append("EXIT - Gasenje sistema\n");
        help.append("RM <fajl/direktorijum> - Uklanjanje fajla/direktorijuma\n");
        help.append("BLOCK <proces> - Blokiranje procesa\n");
        help.append("UNBLOCK <proces> - Odblokiranje procesa\n");
        help.append("TERMINATE <proces> - Terminacija procesa\n");
        help.append("DISKREQ <pozicija> - Dodaj disk zahtjev na poziciju\n");
        help.append("DISKEXECUTE - Izvrsavanje svih zahtjeva po C-SCAN tehnici\n");
        help.append("SHOWBLOCKS <fajl> - Prikaz blokova fajla\n");
        help.append("SHOWFREEBLOCKS - Prikaz slobodnih blokova\n");
        help.append(".. - Povratak na prethodni direktorijum");
        return help.toString();

    }
}