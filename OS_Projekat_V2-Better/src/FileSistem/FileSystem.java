package FileSistem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Rasporedjivanje.Process;
import javafx.scene.control.TextArea;

public class FileSystem {
    private Directory root;
    private Directory currentDir;
    private List<Integer> freeBlocks; // Lista slobodnih blokova
    private TextArea outputArea; // TextArea for logging

    public FileSystem(TextArea outputArea) {
        this.outputArea = outputArea; // Store the TextArea reference
        this.root = new Directory(System.getProperty("user.dir"), null);
        this.currentDir = root;
        this.freeBlocks = new ArrayList<>();
        initializeRoot();
        initializeFreeBlocks();
    }

    private void initializeRoot() {
        String currentWorkingDir = System.getProperty("user.dir");
        logToUI("Trenutni direktorijum: " + currentWorkingDir);
        java.io.File file = new java.io.File(currentWorkingDir);
        initializeDirectory(file, root);
    }

    private void initializeDirectory(java.io.File file, Directory directory) {
        java.io.File[] filesList = file.listFiles();
        if (filesList != null) {
            for (java.io.File f : filesList) {
                if (f.isDirectory()) {
                    Directory subDirectory = new Directory(f.getAbsolutePath(), directory);
                    directory.addSubDirectory(subDirectory);
                    initializeDirectory(f, subDirectory);
                } else {
                    directory.addFile(new File(f.getName(), (int) f.length()));
                }
            }
        }
    }

    private void initializeFreeBlocks() {
        for (int i = 0; i < 100; i++) { // Pretpostavimo da imamo 100 blokova
            freeBlocks.add(i);
        }
    }

    private int allocateBlock() {
        if (freeBlocks.isEmpty()) {
            logToUI("Nema dostupnih blokova za alokaciju.");
            return -1;
        }
        Random rand = new Random();
        return freeBlocks.remove(rand.nextInt(freeBlocks.size()));
    }

    public List<Integer> allocateBlocks(int size) {
        int requiredBlocks = (int) Math.ceil((double) size / Process.BLOCK_SIZE); // Izračunavanje broja potrebnih blokova

        if (!hasSufficientFreeBlocks(requiredBlocks)) {
            logToUI("Nema dovoljno slobodnih blokova za alokaciju.");
            return new ArrayList<>(); // Vraćanje prazne liste
        }

        List<Integer> allocatedBlocks = new ArrayList<>();
        for (int i = 0; i < requiredBlocks; i++) {
            int blockIndex = allocateBlock();
            if (blockIndex != -1) {
                allocatedBlocks.add(blockIndex);
            } else {
                logToUI("Nema dovoljno slobodnih blokova za alokaciju.");
                break;
            }
        }
        return allocatedBlocks;
    }
    private void logToUI(String message) {
        outputArea.appendText(message + "\n");
    }
    public void freeBlocks(List<Integer> blockIndices) {
        freeBlocks.addAll(blockIndices);
    }

    public String changeDirectory(String name) {
        if (name.equals("/")) {
            currentDir = root;
            return "Promenjen direktorijum na root.";
        } else if (name.equals("..")) {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
                return "Vraćeno na prethodni direktorijum.";
            } else {
                return "Nema prethodnog direktorijuma.";
            }
        } else {
            Directory newDir = currentDir.findSubDirectory(name);
            if (newDir != null) {
                currentDir = newDir;
                return "Promenjen direktorijum na: " + getCurrentDirPath();
            } else {
                return "Direktorijum ne postoji.";
            }
        }
    }

    public String handleBackCommand() {
        StringBuilder output = new StringBuilder();

        if (currentDir.getParent() != null) {
            currentDir = currentDir.getParent();
            output.append("Promenjen direktorijum na ").append(getCurrentDirPath()).append("\n");
        } else {
            output.append("Već ste u korenskom direktorijumu\n");
        }

        return output.toString();
    }


    public String listCurrentDirectory() {
        StringBuilder result = new StringBuilder();
        result.append("Lista direktorijuma: ").append(getCurrentDirPath()).append("\n");

        for (Directory dir : currentDir.getSubDirectories()) {
            result.append("[DIR] ").append(dir.getName()).append("\n");
        }
        for (File file : currentDir.getFiles()) {
            result.append(file.getName()).append("\n");
        }

        return result.toString();
    }

    public String createDirectory(String name) {
        // Kreirajte novi direktorijum
        Directory newDir = new Directory(Paths.get(currentDir.getAbsolutePath(), name).toString(), currentDir);
        currentDir.addSubDirectory(newDir);

        // Kreirajte direktorijum na disku
        java.io.File dir = new java.io.File(newDir.getAbsolutePath());
        StringBuilder output = new StringBuilder();
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            if (created) {
                output.append("Kreiran direktorijum: ").append(name);
            } else {
                output.append("Ne može se kreirati direktorijum: ").append(name);
            }
        } else {
            output.append("Direktorijum već postoji: ").append(name);
        }

        return output.toString();
    }


    public String delete(String name) {
        Directory dirToDelete = currentDir.findSubDirectory(name);
        if (dirToDelete != null) {
            currentDir.getSubDirectories().remove(dirToDelete);
            java.io.File dir = new java.io.File(dirToDelete.getAbsolutePath());
            if (dir.exists()) {
                try {
                    Files.delete(Paths.get(dir.getAbsolutePath()));
                } catch (IOException e) {
                    return "Neuspjesno brisanje: " + name;
                }
            }
            return "Obrisan direktorijum: " + name;
        }

        File fileToDelete = currentDir.findFile(name);
        if (fileToDelete != null) {
            currentDir.getFiles().remove(fileToDelete);
            java.io.File file = new java.io.File(Paths.get(currentDir.getAbsolutePath(), name).toString());
            if (file.exists()) {
                try {
                    Files.delete(Paths.get(file.getAbsolutePath()));
                    freeBlocks(fileToDelete.getBlockIndices());
                } catch (IOException e) {
                    return "Neuspjesno brisanje: " + name;
                }
            }
            return "Obrisan fajl: " + name;
        }

        return "Fajl/Direktorijum sa takvim nazivom ne postoji.";
    }


    public String getCurrentDirPath() {
        return currentDir.getAbsolutePath();
    }

    public String showFreeBlocks() {
        return "Slobodni blokovi: " + freeBlocks;
    }


    public boolean hasSufficientFreeBlocks(int requiredBlocks) {
        return freeBlocks.size() >= requiredBlocks;
    }
    public String printDirectoryTree() {
        return printDirectoryTree(currentDir, "");
    }

    private String printDirectoryTree(Directory directory, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(directory.getName()).append("\n");

        List<Directory> subDirs = directory.getSubDirectories();
        List<File> files = directory.getFiles();

        for (int i = 0; i < subDirs.size(); i++) {
            Directory subDir = subDirs.get(i);
            if (i == subDirs.size() - 1 && files.isEmpty()) {
                sb.append(printDirectoryTree(subDir, indent + "   "));
            } else {
                sb.append(printDirectoryTree(subDir, indent + "│   "));
            }
        }

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (i == files.size() - 1) {
                sb.append(indent).append("└── ").append(file.getName()).append("\n");
            } else {
                sb.append(indent).append("├── ").append(file.getName()).append("\n");
            }
        }

        return sb.toString();
    }

}
