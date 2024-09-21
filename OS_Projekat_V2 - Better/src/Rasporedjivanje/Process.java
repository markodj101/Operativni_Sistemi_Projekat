package Rasporedjivanje;

import Assembler.Assembler;

import java.util.ArrayList;
import java.util.List;

public class Process extends Thread {
    private final String name;
    private final String content;
    private final Assembler assembler;
    private final ProcessScheduler processScheduler;
    private List<Integer> blockIndices; // Lista indeksa blokova koji čine proces
    public static final int BLOCK_SIZE = 10; // 10 bajtova
    private boolean blocked;

    public Process(String name, String content, Assembler assembler, ProcessScheduler processScheduler) {
        this.name = name;
        this.content = content;
        this.assembler = assembler;
        this.processScheduler = processScheduler;
        this.blockIndices = new ArrayList<>();
        this.blocked = false; // Inicijalno proces nije blokiran
    }

    public String getProcessName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public List<Integer> getBlockIndices() {
        return blockIndices;
    }

    public void setBlockIndices(List<Integer> blockIndices) {
        this.blockIndices = blockIndices;
    }

    public int getMemorySize() {
        return content.getBytes().length; // Veličina memorije na osnovu dužine sadržaja u bajtovima
    }

    public int getRequiredBlocks() {
        System.out.println("Ukupan broj blokova koje proces zauzima: " + (int) Math.ceil((double) getMemorySize() / BLOCK_SIZE));
        System.out.println("Memorijska veličina procesa: " + getMemorySize() + " bajtova");
        return (int) Math.ceil((double) getMemorySize() / BLOCK_SIZE);
    }

    public int getStartAddress() {
        return blockIndices.isEmpty() ? -1 : blockIndices.get(0);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public void run() {
        try {
            assembler.runInstructions(content);
            processScheduler.processFinished();
        } catch (InterruptedException e) {
            System.out.println("Proces " + name + " je prekinut.");
        }
    }
}
