package FileSistem;

import java.util.ArrayList;
import java.util.List;

public class File {
    private String name;
    private int size;
    private List<Integer> blockIndices; // Lista indeksa blokova koji čine fajl

    public File(String name, int size) {
        this.name = name;
        this.size = size;
        this.blockIndices = new ArrayList<>();
    }

    public String getName() {
        return name;
    }


    public List<Integer> getBlockIndices() {
        return blockIndices;
    }

}
