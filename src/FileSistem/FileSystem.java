package FileSistem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSystem {
    private File currentDir;

    public FileSystem() {
        this.currentDir = new File(System.getProperty("user.dir"));
    }

    public void changeDirectory(String name) {
        File newDir = new File(currentDir, name);
        if (newDir.exists() && newDir.isDirectory()) {
            currentDir = newDir;
            System.out.println("Changed directory to " + currentDir.getAbsolutePath());
        } else {
            System.out.println("Directory does not exist.");
        }
    }

    public void listCurrentDirectory() {
        File[] filesList = currentDir.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                if (file.isDirectory()) {
                    System.out.println("[DIR] " + file.getName());
                } else {
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("Unable to list files in the directory.");
        }
    }

    public void createDirectory(String name) {
        File newDir = new File(currentDir, name);
        if (!newDir.exists()) {
            newDir.mkdir();
            System.out.println("Directory created: " + newDir.getName());
        } else {
            System.out.println("Directory already exists.");
        }
    }

    public void delete(String name) {
        File fileToDelete = new File(currentDir, name);
        if (fileToDelete.exists()) {
            try {
                if (fileToDelete.isDirectory()) {
                    Files.delete(Paths.get(fileToDelete.getAbsolutePath()));
                } else {
                    Files.delete(Paths.get(fileToDelete.getAbsolutePath()));
                }
                System.out.println("Deleted: " + name);
            } catch (IOException e) {
                System.out.println("Failed to delete: " + name);
            }
        } else {
            System.out.println("File/Directory does not exist.");
        }
    }

    public File getCurrentDir() {
        return currentDir;
    }
}
