package Disk;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Collections;

public class DiskScheduler {
    private final int diskSize;
    private int currentPosition;
    private final Queue<Integer> requestQueue;

    public DiskScheduler(int diskSize) {
        this.diskSize = diskSize;
        this.currentPosition = 0;
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(int position) {
        if (position >= 0 && position < diskSize) {
            requestQueue.add(position);
            System.out.println("Dodan disk zahtev na poziciji: " + position);
        } else {
            System.out.println("Nevažeća disk pozicija " + position);
        }
    }

    public void executeRequests() {
        if (requestQueue.isEmpty()) {
            System.out.println("Nema disk zahteva za izvršavanje");
            return;
        }

        System.out.println("Izvršavanje zahteva diska koristeći C-SCAN tehniku...");
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

        // Zahtjevi procesa koji se krecu desno od trenutne pozicije diska
        for (int request : right) {
            System.out.println("Usluživanje zahteva na poziciji " + request);
            currentPosition = request;
        }

        // Prelazak na drugi kraj diska
        if (!left.isEmpty()) {
            System.out.println("Prelazak na drugi kraj diska...");
            currentPosition = 0;
            for (int request : left) {
                System.out.println("Usluživanje zahteva na poziciji " + request);
                currentPosition = request;
            }
        }

        requestQueue.clear();
        System.out.println("Svi zahtevi diska su usluženi.");
    }
}
