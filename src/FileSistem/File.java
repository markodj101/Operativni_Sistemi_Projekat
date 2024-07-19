package FileSistem;

public class File {
    private String name;
    private String content;
    private boolean isDirectory;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
        this.isDirectory = false;
    }

    public File(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.content = "";
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
