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

    public String addRequest(int position) {
        if (position >= 0 && position < diskSize) {
            requestQueue.add(position);
            return "Dodan disk zahtjev na poziciji: " + position;
        } else {
            return "Nevazeca disk pozicija " + position;
        }
    }


    public String executeRequests() {
        if (requestQueue.isEmpty()) {
            return "Nema disk zahtjeva za izvrsavanje";
        }

        StringBuilder result = new StringBuilder();
        result.append("Izvrsavanje zahtjeva diska koristeci C-SCAN tehniku...\n");

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

        for (int request : right) {
            result.append("Usluzivanje zahtjeva na poziciji ").append(request).append("\n");
            currentPosition = request;
        }

        if (!left.isEmpty()) {
            result.append("Prelazak na drugi kraj diska...\n");
            currentPosition = 0;
            for (int request : left) {
                result.append("Usluzivanje zahtjeva na poziciji ").append(request).append("\n");
                currentPosition = request;
            }
        }

        requestQueue.clear();
        result.append("Svi zahtjevi diska su usluzeni.");
        return result.toString();
    }

}
