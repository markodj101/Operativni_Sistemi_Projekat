package FileSistem;

import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    private Map<String, File> files;

    public FileSystem() {
        files = new HashMap<>();
    }

    public void createFile(String name, String content) {
        files.put(name, new File(name, content));
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public void deleteFile(String name) {
        files.remove(name);
    }

    public void listFiles() {
        for (String name : files.keySet()) {
            System.out.println(name);
        }
    }
}
