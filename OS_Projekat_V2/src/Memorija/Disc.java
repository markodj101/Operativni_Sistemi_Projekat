package Memorija;

import java.util.ArrayList;
import java.util.Iterator;

public class Disc {
    public static final int SIZE = 1024;
    private static ArrayList<Block> memoryBlocks = new ArrayList<>();

    static {
        memoryBlocks.add(new Block(0, SIZE)); // Initial single large free block
    }

    public static void allocateMemory(String fileName, int size) {
        for (Block block : memoryBlocks) {
            if (!block.isOccupied() && block.getSize() >= size) {
                int remainingSize = block.getSize() - size;
                block.setSize(size);
                block.setOccupied(true);
                block.setFileName(fileName);

                if (remainingSize > 0) {
                    Block newBlock = new Block(block.getAddress() + size, remainingSize);
                    memoryBlocks.add(memoryBlocks.indexOf(block) + 1, newBlock);
                }
                return;
            }
        }
        System.out.println("Not enough memory to allocate " + size + " units.");
    }

    public static void deallocateMemory(String fileName) {
        for (Block block : memoryBlocks) {
            if (block.isOccupied() && block.getFileName().equals(fileName)) {
                block.setOccupied(false);
                block.setFileName(null);
                mergeFreeBlocks();
                return;
            }
        }
        System.out.println("File not found in memory: " + fileName);
    }

    private static void mergeFreeBlocks() {
        Iterator<Block> iterator = memoryBlocks.iterator();
        Block previous = null;

        while (iterator.hasNext()) {
            Block current = iterator.next();
            if (previous != null && !previous.isOccupied() && !current.isOccupied()) {
                previous.setSize(previous.getSize() + current.getSize());
                iterator.remove();
            } else {
                previous = current;
            }
        }
    }

    public static void defragment() {
        ArrayList<Block> newBlocks = new ArrayList<>();
        int newAddress = 0;

        for (Block block : memoryBlocks) {
            if (block.isOccupied()) {
                block.setAddress(newAddress);
                newBlocks.add(block);
                newAddress += block.getSize();
            }
        }

        if (newAddress < SIZE) {
            newBlocks.add(new Block(newAddress, SIZE - newAddress));
        }

        memoryBlocks = newBlocks;
    }

    public static void printMemory() {
        for (Block block : memoryBlocks) {
            System.out.println(block);
        }
    }
}

