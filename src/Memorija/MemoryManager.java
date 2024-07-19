package Memorija;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private int totalMemory;
    private List<MemoryBlock> memoryBlocks;

    public MemoryManager(int totalMemory) {
        this.totalMemory = totalMemory;
        this.memoryBlocks = new ArrayList<>();
        this.memoryBlocks.add(new MemoryBlock(0, totalMemory)); // initially all memory is free
    }

    public int allocateMemory(int size) {
        for (MemoryBlock block : memoryBlocks) {
            if (!block.isAllocated() && block.getSize() >= size) {
                int startAddress = block.getStartAddress();
                if (block.getSize() > size) {
                    memoryBlocks.add(new MemoryBlock(startAddress + size, block.getSize() - size));
                }
                block.setSize(size);
                block.setAllocated(true);
                return startAddress;
            }
        }
        return -1; // not enough memory
    }

    public void freeMemory(int startAddress) {
        for (MemoryBlock block : memoryBlocks) {
            if (block.getStartAddress() == startAddress && block.isAllocated()) {
                block.setAllocated(false);
                defragmentMemory();
                return;
            }
        }
    }

    private void defragmentMemory() {
        List<MemoryBlock> newBlocks = new ArrayList<>();
        MemoryBlock previous = null;
        for (MemoryBlock block : memoryBlocks) {
            if (previous != null && !previous.isAllocated() && !block.isAllocated()) {
                previous.setSize(previous.getSize() + block.getSize());
            } else {
                newBlocks.add(block);
                previous = block;
            }
        }
        memoryBlocks = newBlocks;
    }

    public int getFreeMemory() {
        int freeMemory = 0;
        for (MemoryBlock block : memoryBlocks) {
            if (!block.isAllocated()) {
                freeMemory += block.getSize();
            }
        }
        return freeMemory;
    }
}
