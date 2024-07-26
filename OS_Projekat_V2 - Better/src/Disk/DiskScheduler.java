package Disk;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.Collections;

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
            System.out.println("Dodan disk zahtjev na poziciji: " + position);
        } else {
            System.out.println("Nevazeca disk pozicija " + position);
        }
    }

    public void executeRequests() {
        if (requestQueue.isEmpty()) {
            System.out.println("Nema disk zahtjeva za izvrsavanje");
            return;
        }

        System.out.println("Izvrsavanje zahtjeva diska koristeci C-SCAN tehniku...");
        List<Integer> requests = new LinkedList<>(requestQueue);
        Collections.sort(requests);

        List<Integer> left = new LinkedList<>();
        List<Integer> right = new LinkedList<>();

        for (int request : requests) {
            if (request < currentPosition) {
                left.add(request);
            } else {
                right.add(request);
            }
        }

        // Process requests to the right of the current position
        for (int request : right) {
            System.out.println("Usluzivanje zahtjeva na poziciji " + request);
            currentPosition = request;
        }

        // Wrap around to the beginning of the disk
        if (!left.isEmpty()) {
            System.out.println("Prelazak na drugi kraj diska...");
            currentPosition = 0;
            for (int request : left) {
                System.out.println("Usluzivanje zahtjeva na poziciji " + request);
                currentPosition = request;
            }
        }

        requestQueue.clear();
        System.out.println("Svi zahtjevi diska su usluzeni.");
    }
}
