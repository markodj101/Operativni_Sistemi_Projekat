package Memorija;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoryManager {
    private List<MemoryBlock> freeBlocks;

    public MemoryManager(int totalMemory) {
        freeBlocks = new ArrayList<>();
        freeBlocks.add(new MemoryBlock(0, totalMemory));
    }

    public boolean allocateMemory(int size) {
        for (MemoryBlock block : freeBlocks) {
            if (block.size >= size) {
                freeBlocks.remove(block);
                if (block.size > size) {
                    freeBlocks.add(new MemoryBlock(block.start + size, block.size - size));
                }
                return true;
            }
        }
        return false;
    }

    public void freeMemory(int start, int size) {
        freeBlocks.add(new MemoryBlock(start, size));
    }

    public void defragment() {
        // Sort the free blocks by their start address
        Collections.sort(freeBlocks, Comparator.comparingInt(block -> block.start));

        // Combine adjacent free blocks
        List<MemoryBlock> newFreeBlocks = new ArrayList<>();
        MemoryBlock current = freeBlocks.get(0);

        for (int i = 1; i < freeBlocks.size(); i++) {
            MemoryBlock next = freeBlocks.get(i);
            if (current.start + current.size == next.start) {
                // Adjacent blocks, combine them
                current.size += next.size;
            } else {
                // Not adjacent, add the current block to the list and move to the next
                newFreeBlocks.add(current);
                current = next;
            }
        }
        // Add the last block
        newFreeBlocks.add(current);

        // Replace the old free blocks list with the new one
        freeBlocks = newFreeBlocks;
    }

    public void printFreeBlocks() {
        for (MemoryBlock block : freeBlocks) {
            System.out.println("Free block: start=" + block.start + ", size=" + block.size);
        }
    }

    public static void main(String[] args) {
        MemoryManager memoryManager = new MemoryManager(100);

        memoryManager.allocateMemory(20);
        memoryManager.allocateMemory(10);
        memoryManager.freeMemory(0, 20);
        memoryManager.freeMemory(30, 10);

        System.out.println("Before defragmentation:");
        memoryManager.printFreeBlocks();

        memoryManager.defragment();

        System.out.println("After defragmentation:");
        memoryManager.printFreeBlocks();
    }
}