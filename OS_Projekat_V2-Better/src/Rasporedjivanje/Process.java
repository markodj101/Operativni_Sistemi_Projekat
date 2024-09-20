package Rasporedjivanje;

import Assembler.Assembler;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

public class Process extends Thread {
    private String name;
    private String content;
    private Assembler assembler;
    private ProcessScheduler processScheduler;
    private List<Integer> blockIndices; // Lista indeksa blokova koji čine proces
    public static final int BLOCK_SIZE = 4; // 4 bajta
    private boolean blocked;
    private TextArea outputArea;

    public Process(String name, String content, Assembler assembler, ProcessScheduler processScheduler, TextArea outputArea) {
        this.name = name;
        this.content = content;
        this.assembler = assembler;
        this.processScheduler = processScheduler;
        this.blockIndices = new ArrayList<>();
        this.blocked = false; // Inicijalno proces nije blokiran
        this.outputArea = outputArea;
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
        return content.getBytes().length; // Velicina memorije na osnovu duzine sadrzaja u bajtovima
    }

    public int getRequiredBlocks() {
        logToUI((int) Math.ceil((double) getMemorySize() / BLOCK_SIZE));
        logToUI("VELICINA: " + getMemorySize());
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
            logToUI("Proces " + name + " je prekinut.");
        }
    }


    private void logToUI(String message) {
        outputArea.appendText(message + "\n");
    }


    private void logToUI(int message) {
        outputArea.appendText(String.valueOf(message) + "\n");
    }
}
