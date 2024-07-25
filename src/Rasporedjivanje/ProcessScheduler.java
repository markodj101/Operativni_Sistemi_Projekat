package Rasporedjivanje;

import Assembler.Assembler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessScheduler {
    private Queue<Process> processQueue;
    private Queue<Process> blockedQueue;
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    private Assembler assembler;
    private Process runningProcess;

    public ProcessScheduler(MemoryManager memoryManager, FileSystem fileSystem) {
        this.processQueue = new LinkedList<>();
        this.blockedQueue = new LinkedList<>();
        this.memoryManager = memoryManager;
        this.fileSystem = fileSystem;
        this.runningProcess = null;
    }

    public void setAssembler(Assembler assembler) {
        this.assembler = assembler;
    }

    public void addProcess(String name, String content) {
        Process process = new Process(name, content);
        int requiredBlocks = process.getRequiredBlocks();

        if (!fileSystem.hasSufficientFreeBlocks(requiredBlocks)) {
            System.out.println("Nema dovoljno slobodnih blokova za proces " + name);
            return;
        }

        List<Integer> blockIndices = fileSystem.allocateBlocks(process.getMemorySize());
        if (blockIndices.isEmpty()) {
            System.out.println("Nema dovoljno slobodnih blokova za proces " + name);
            return;
        }

        process.setBlockIndices(blockIndices);
        processQueue.add(process);
        System.out.println("Proces " + name + " je dodan u red čekanja sa početnom adresom " + process.getStartAddress());
        executeNextProcess();
    }


    private void executeNextProcess() {
        if (runningProcess == null && !processQueue.isEmpty()) {
            runningProcess = processQueue.poll();
            int memorySize = runningProcess.getMemorySize();
            int startAddress = memoryManager.allocateMemory(memorySize, runningProcess.getStartAddress());
            if (startAddress != -1) {
                System.out.println("Proces " + runningProcess.getName() + " je započet sa početnom adresom " + runningProcess.getStartAddress());
                assembler.runInstructions(runningProcess.getContent());
            } else {
                System.out.println("Nedovoljno memorije za pokretanje procesa " + runningProcess.getName());
                processQueue.add(runningProcess); // Vraćanje procesa u red čekanja
                runningProcess = null;
            }
        }
    }

    public void blockProcess(String name) {
        if (runningProcess != null && runningProcess.getName().equals(name)) {
            memoryManager.freeMemory(runningProcess.getStartAddress());
            System.out.println("Proces " + name + " je sada blokiran i memorija je oslobođena.");
            runningProcess.setBlocked(true);
            blockedQueue.add(runningProcess);
            runningProcess = null;
            executeNextProcess();
        } else {
            System.out.println("Proces " + name + " nije pronađen ili nije pokrenut.");
        }
    }

    public void unblockProcess(String name) {
        for (Process process : blockedQueue) {
            if (process.getName().equals(name)) {
                blockedQueue.remove(process);
                processQueue.add(process);
                System.out.println("Proces " + name + " je sada odblokiran i dodat u red čekanja.");
                executeNextProcess();
                return;
            }
        }
        System.out.println("Proces " + name + " nije pronađen u redu blokiranih procesa.");
    }

    public void terminateProcess(String name) {
        if (runningProcess != null && runningProcess.getName().equals(name)) {
            memoryManager.freeMemory(runningProcess.getStartAddress());
            fileSystem.freeBlocks(runningProcess.getBlockIndices());
            System.out.println("Proces " + name + " je terminiran i memorija je oslobođena.");
            runningProcess = null;
            executeNextProcess();
        } else {
            boolean found = false;
            for (Process process : processQueue) {
                if (process.getName().equals(name)) {
                    processQueue.remove(process);
                    fileSystem.freeBlocks(process.getBlockIndices());
                    System.out.println("Proces " + name + " je uklonjen iz reda čekanja i memorija je oslobođena.");
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (Process process : blockedQueue) {
                    if (process.getName().equals(name)) {
                        blockedQueue.remove(process);
                        fileSystem.freeBlocks(process.getBlockIndices());
                        System.out.println("Proces " + name + " je uklonjen iz reda blokiranih procesa i memorija je oslobođena.");
                        break;
                    }
                }
            }
        }
    }

    public void showProcessList() {
        if (processQueue.isEmpty() && runningProcess == null) {
            System.out.println("Trenutno nema nijednog pokrenutog procesa.");

        }
        if (runningProcess != null) {
            System.out.println("Pokrenut proces " + runningProcess.getName() + ":");
            System.out.println("Instrukcije: \n" + runningProcess.getContent());
        }
        System.out.println("Procesi koji cekaju u redu: ");
        for (Process process : processQueue) {
            System.out.println("Proces " + process.getName());
            //System.out.println("Instrukcije: \n" + process.getContent());
        }
        for (Process process : blockedQueue) {
            System.out.println("Blokiran proces " + process.getName());
            //System.out.println("Instrukcije: \n" + process.getContent());
        }
    }

    public int getMemoryUsage() {
        int usedMemory = 0;
        if (runningProcess != null) {
            usedMemory += runningProcess.getMemorySize();
        }
        for (Process process : processQueue) {
            if (!process.isBlocked()) {
                //usedMemory += process.getMemorySize(); // ZAKOMENTARISI
            }
        }
        return usedMemory;
    }

    public void showProcessBlocks(String processName) {
        if (runningProcess != null && runningProcess.getName().equals(processName)) {
            System.out.println("Blokovi za proces " + processName + ": " + runningProcess.getBlockIndices());
        } else {
            for (Process process : processQueue) {
                if (process.getName().equals(processName)) {
                    System.out.println("Blokovi za proces " + processName + ": " + process.getBlockIndices());
                    return;
                }
            }
            for (Process process : blockedQueue) {
                if (process.getName().equals(processName)) {
                    System.out.println("Blokovi za proces " + processName + ": " + process.getBlockIndices());
                    return;
                }
            }
            System.out.println("Proces " + processName + " nije pronađen.");
        }
    }
}
//modified!!