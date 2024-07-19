package Rasporedjivanje;

public class Process {
    private String name;
    private int startAddress;
    private int memorySize;
    private String content;

    public Process(String name, int startAddress, int memorySize, String content) {
        this.name = name;
        this.startAddress = startAddress;
        this.memorySize = memorySize;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public String getContent() {
        return content;
    }
}
