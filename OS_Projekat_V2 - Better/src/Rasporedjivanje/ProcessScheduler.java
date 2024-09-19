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

    public synchronized String blockProcess(String name) {
        if (runningProcess != null && runningProcess.getProcessName().equals(name)) {
            runningProcess.interrupt();
            memoryManager.freeMemory(runningProcess.getStartAddress());
            runningProcess.setBlocked(true);
            blockedQueue.add(runningProcess);
            runningProcess = null;
            executeNextProcess();
            return "Proces " + name + " je sada blokiran i memorija je oslobođena.";
        } else {
            return "Proces " + name + " nije pronađen ili nije pokrenut.";
        }
    }


    public synchronized String unblockProcess(String name) {
        for (Process process : blockedQueue) {
            if (process.getProcessName().equals(name)) {
                blockedQueue.remove(process);
                // Kreiraj novu instancu procesa
                Process newProcess = new Process(process.getProcessName(), process.getContent(), assembler, this);
                newProcess.setBlockIndices(process.getBlockIndices());
                processQueue.add(newProcess);
                executeNextProcess();
                return "Proces " + name + " je sada odblokiran i dodat u red čekanja.";
            }
        }
        return "Proces " + name + " nije pronađen u redu blokiranih procesa.";
    }


    public synchronized String terminateProcess(String name) {
        if (runningProcess != null && runningProcess.getProcessName().equals(name)) {
            runningProcess.interrupt();
            memoryManager.freeMemory(runningProcess.getStartAddress());
            fileSystem.freeBlocks(runningProcess.getBlockIndices());
            runningProcess = null;
            executeNextProcess();
            return "Proces " + name + " je terminiran i memorija je oslobođena.";
        } else {
            boolean found = false;
            for (Process process : processQueue) {
                if (process.getProcessName().equals(name)) {
                    processQueue.remove(process);
                    fileSystem.freeBlocks(process.getBlockIndices());
                    found = true;
                    return "Proces " + name + " je uklonjen iz reda čekanja i memorija je oslobođena.";
                }
            }
            for (Process process : blockedQueue) {
                if (process.getProcessName().equals(name)) {
                    blockedQueue.remove(process);
                    fileSystem.freeBlocks(process.getBlockIndices());
                    return "Proces " + name + " je uklonjen iz reda blokiranih procesa i memorija je oslobođena.";
                }
            }
            return "Proces " + name + " nije pronađen.";
        }
    }


    public synchronized String showProcessList() {
        StringBuilder output = new StringBuilder();

        if (processQueue.isEmpty() && runningProcess == null) {
            output.append("Trenutno nema nijednog pokrenutog procesa.\n");
        } else {
            if (runningProcess != null) {
                output.append("Pokrenut proces ").append(runningProcess.getProcessName()).append(":\n");
                output.append("Instrukcije: \n").append(runningProcess.getContent()).append("\n");
            }

            output.append("Procesi koji čekaju u redu: \n");
            if (!processQueue.isEmpty()) {
                for (Process process : processQueue) {
                    output.append("Proces ").append(process.getProcessName()).append("\n");
                }
            } else {
                output.append("NaN\n");
            }

            output.append("Blokirani procesi: \n");
            if (!blockedQueue.isEmpty()) {
                for (Process process : blockedQueue) {
                    output.append("Proces ").append(process.getProcessName()).append("\n");
                }
            } else {
                output.append("NaN\n");
            }
        }

        return output.toString();
    }


    public synchronized int getMemoryUsage() {
        int usedMemory = 0;
        if (runningProcess != null) {
            usedMemory += runningProcess.getMemorySize();
        }
        return usedMemory;
    }

    public synchronized String showProcessBlocks(String processName) {
        StringBuilder result = new StringBuilder();
        if (runningProcess != null && runningProcess.getProcessName().equals(processName)) {
            result.append("Blokovi za proces ").append(processName).append(": ").append(runningProcess.getBlockIndices());
        } else {
            boolean found = false;
            for (Process process : processQueue) {
                if (process.getProcessName().equals(processName)) {
                    result.append("Blokovi za proces ").append(processName).append(": ").append(process.getBlockIndices());
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (Process process : blockedQueue) {
                    if (process.getProcessName().equals(processName)) {
                        result.append("Blokovi za proces ").append(processName).append(": ").append(process.getBlockIndices());
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                result.append("Proces ").append(processName).append(" nije pronađen.");
            }
        }
        return result.toString();
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
