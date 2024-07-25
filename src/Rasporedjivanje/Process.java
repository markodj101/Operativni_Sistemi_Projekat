package Rasporedjivanje;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private String name;
    private String content;
    private boolean blocked;
    private List<Integer> blockIndices; // Lista indeksa blokova koji čine proces
    private static final int BLOCK_SIZE = 4096; // 4KB po bloku

    public Process(String name, String content) {
        this.name = name;
        this.content = content;
        this.blocked = false; // Inicijalno proces nije blokiran
        this.blockIndices = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
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
        return (int) Math.ceil((double) getMemorySize() / BLOCK_SIZE);
    }

    public int getStartAddress() {
        return blockIndices.isEmpty() ? -1 : blockIndices.get(0);
    }
}
