package Rasporedjivanje;

import Memorija.MemoryManager;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessScheduler {
    private Queue<Process> processQueue;
    private MemoryManager memoryManager;

    public ProcessScheduler(MemoryManager memoryManager) {
        this.processQueue = new LinkedList<>();
        this.memoryManager = memoryManager;
    }

    public void addProcess(String name, String content, int memorySize) {
        int startAddress = memoryManager.allocateMemory(memorySize);
        if (startAddress != -1) {
            Process process = new Process(name, startAddress, memorySize, content);
            processQueue.add(process);
            System.out.println("Process " + name + " added with start address " + startAddress);
            executeProcess(process);
        } else {
            System.out.println("Not enough memory to add process " + name);
        }
    }

    private void executeProcess(Process process) {
        String[] instructions = process.getContent().split("\n");
        for (String instruction : instructions) {
            System.out.println("Executing instruction: " + instruction);
            // Dodaj logiku za izvršenje instrukcija ovde
        }
    }

    public void showProcessList() {
        for (Process process : processQueue) {
            System.out.println("Process " + process.getName() + ":");
            System.out.println("  Start Address: " + process.getStartAddress());
            System.out.println("  Memory Size: " + process.getMemorySize());
            System.out.println("  Instructions: " + process.getContent());
        }
    }

    public int getMemoryUsage() {
        int usedMemory = 0;
        for (Process process : processQueue) {
            usedMemory += process.getMemorySize();
        }
        return usedMemory;
    }
}
