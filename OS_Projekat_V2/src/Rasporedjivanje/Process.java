package Rasporedjivanje;

public class Process {
    private String name;
    private int duration; // Trajanje procesa u milisekundama

    public Process(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void run() {
        System.out.println("Process " + name + " is starting.");
        try {
            Thread.sleep(duration); // Simulacija vremena izvršavanja procesa
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process " + name + " has finished.");
    }
}
