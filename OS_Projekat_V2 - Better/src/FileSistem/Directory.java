package FileSistem;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String name;
    private String absolutePath;
    private Directory parent;
    private List<Directory> subDirectories;
    private List<File> files;

    public Directory(String absolutePath, Directory parent) {
        this.absolutePath = absolutePath;
        this.name = new java.io.File(absolutePath).getName();
        this.parent = parent;
        this.subDirectories = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Directory getParent() {
        return parent;
    }

    public List<Directory> getSubDirectories() {
        return subDirectories;
    }

    public List<File> getFiles() {
        return files;
    }

    public void addSubDirectory(Directory dir) {
        subDirectories.add(dir);
    }

    public void addFile(File file) {
        files.add(file);
    }

    public Directory findSubDirectory(String name) {
        for (Directory dir : subDirectories) {
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }

    public File findFile(String name) {
        for (File file : files) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        return null;
    }
}
