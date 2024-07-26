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

    public synchronized void addProcess(String name, String content) {
        if (isProcessRunningOrQueued(name)) {
            System.out.println("Proces " + name + " je već pokrenut ili u redu za čekanje.");
            return;
        }

        Process process = new Process(name, content, assembler, this);
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

    private boolean isProcessRunningOrQueued(String name) {
        if (runningProcess != null && runningProcess.getProcessName().equals(name)) {
            return true;
        }
        for (Process process : processQueue) {
            if (process.getProcessName().equals(name)) {
                return true;
            }
        }
        for (Process process : blockedQueue) {
            if (process.getProcessName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void executeNextProcess() {
        if (runningProcess == null && !processQueue.isEmpty()) {
            runningProcess = processQueue.poll();
            int memorySize = runningProcess.getMemorySize();
            int startAddress = memoryManager.allocateMemory(memorySize, runningProcess.getStartAddress());
            if (startAddress != -1) {
                System.out.println("Proces " + runningProcess.getProcessName() + " je započet sa početnom adresom " + runningProcess.getStartAddress());
                runningProcess.start();
            } else {
                System.out.println("Nedovoljno memorije za pokretanje procesa " + runningProcess.getProcessName());
                processQueue.add(runningProcess); // Vraćanje procesa u red čekanja
                runningProcess = null;
            }
        }
    }

    public synchronized void blockProcess(String name) {
        if (runningProcess != null && runningProcess.getProcessName().equals(name)) {
            runningProcess.interrupt();
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

    public synchronized void unblockProcess(String name) {
        for (Process process : blockedQueue) {
            if (process.getProcessName().equals(name)) {
                blockedQueue.remove(process);
                // Create a new instance of the process
                Process newProcess = new Process(process.getProcessName(), process.getContent(), assembler, this);
                newProcess.setBlockIndices(process.getBlockIndices());
                processQueue.add(newProcess);
                System.out.println("Proces " + name + " je sada odblokiran i dodat u red čekanja.");
                executeNextProcess();
                return;
            }
        }
        System.out.println("Proces " + name + " nije pronađen u redu blokiranih procesa.");
    }

    public synchronized void terminateProcess(String name) {
        if (runningProcess != null && runningProcess.getProcessName().equals(name)) {
            runningProcess.interrupt();
            memoryManager.freeMemory(runningProcess.getStartAddress());
            fileSystem.freeBlocks(runningProcess.getBlockIndices());
            System.out.println("Proces " + name + " je terminiran i memorija je oslobođena.");
            runningProcess = null;
            executeNextProcess();
        } else {
            boolean found = false;
            for (Process process : processQueue) {
                if (process.getProcessName().equals(name)) {
                    processQueue.remove(process);
                    fileSystem.freeBlocks(process.getBlockIndices());
                    System.out.println("Proces " + name + " je uklonjen iz reda čekanja i memorija je oslobođena.");
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (Process process : blockedQueue) {
                    if (process.getProcessName().equals(name)) {
                        blockedQueue.remove(process);
                        fileSystem.freeBlocks(process.getBlockIndices());
                        System.out.println("Proces " + name + " je uklonjen iz reda blokiranih procesa i memorija je oslobođena.");
                        break;
                    }
                }
            }
        }
    }

    public synchronized void showProcessList() {
        if (processQueue.isEmpty() && runningProcess == null) {
            System.out.println("Trenutno nema nijednog pokrenutog procesa.");
        }
        if (runningProcess != null) {
            System.out.println("Pokrenut proces " + runningProcess.getProcessName() + ":");
            System.out.println("Instrukcije: \n" + runningProcess.getContent());
        }
        System.out.println("Procesi koji cekaju u redu: ");
        if (!processQueue.isEmpty()){
            for (Process process : processQueue) {
                System.out.println("Proces " + process.getProcessName());
            }
        }
        else
            System.out.println("NaN");

        System.out.println("Blokirani procesi: ");
        if (!blockedQueue.isEmpty()){
            for (Process process : blockedQueue) {
                System.out.println("Proces " + process.getProcessName());
            }
        }
        else
            System.out.println("NaN");

    }

    public synchronized int getMemoryUsage() {
        int usedMemory = 0;
        if (runningProcess != null) {
            usedMemory += runningProcess.getMemorySize();
        }
        return usedMemory;
    }

    public synchronized void showProcessBlocks(String processName) {
        if (runningProcess != null && runningProcess.getProcessName().equals(processName)) {
            System.out.println("Blokovi za proces " + processName + ": " + runningProcess.getBlockIndices());
        } else {
            for (Process process : processQueue) {
                if (process.getProcessName().equals(processName)) {
                    System.out.println("Blokovi za proces " + processName + ": " + process.getBlockIndices());
                    return;
                }
            }
            for (Process process : blockedQueue) {
                if (process.getProcessName().equals(processName)) {
                    System.out.println("Blokovi za proces " + processName + ": " + process.getBlockIndices());
                    return;
                }
            }
            System.out.println("Proces " + processName + " nije pronađen.");
        }
    }

    public synchronized void processFinished() {
        if (runningProcess != null) {
            System.out.println("Proces " + runningProcess.getProcessName() + " je završen.");
            memoryManager.freeMemory(runningProcess.getStartAddress());
            fileSystem.freeBlocks(runningProcess.getBlockIndices());
            runningProcess = null;
            executeNextProcess();
        }
    }
}
