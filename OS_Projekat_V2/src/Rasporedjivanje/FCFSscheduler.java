package Rasporedjivanje;

import java.util.LinkedList;
import java.util.Queue;

public class FCFSscheduler {
    private Queue<Process> processQueue;

    public FCFSscheduler() {
        processQueue = new LinkedList<>();
    }

    public void addProcess(Process process) {
        processQueue.add(process);
    }

    public void run() {
        while (!processQueue.isEmpty()) {
            Process process = processQueue.poll();
            process.run();
        }
    }
}

