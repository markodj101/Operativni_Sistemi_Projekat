package Disk;

import java.util.LinkedList;
import java.util.Queue;

public class DiskScheduler {
    private int diskSize;
    private int currentPosition;
    private Queue<Integer> requestQueue;

    public DiskScheduler(int diskSize) {
        this.diskSize = diskSize;
        this.currentPosition = 0;
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(int position) {
        if (position >= 0 && position < diskSize) {
            requestQueue.add(position);
        } else {
            System.out.println("Invalid disk position.");
        }
    }

    public void executeRequests() {
        while (!requestQueue.isEmpty()) {
            int nextPosition = requestQueue.poll();
            System.out.println("Servicing request at position " + nextPosition);
            currentPosition = nextPosition;
        }
    }
}
