package Commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {
    private File currentDir;

    public CommandExecutor() {
        this.currentDir = new File("."); // Inicijalni direktorijum je trenutni direktorijum
    }

    public void execute(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];
        List<String> args = Arrays.asList(parts).subList(1, parts.length);

        switch (cmd.toLowerCase()) {
            case "dir":
                handleDirCommand();
                break;
            case "mkdir":
                handleMkdirCommand(args);
                break;
            case "run":
                handleRunCommand(args);
                break;
            case "tree":
                handleTreeCommand(currentDir);
                break;
            case "del":
                handleDelCommand(args);
                break;
            case "cd":
                handleCdCommand(args);
                break;
            case "ls":
                handleLsCommand();
                break;
            case "..":
                handleBackCommand();
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    private void handleBackCommand() {
        File parentDir = currentDir.getParentFile();
        if(parentDir != null && parentDir.exists()) {
            currentDir = parentDir;
            System.out.println("Changed directory to " + currentDir.getAbsolutePath());
        }
        else {
            System.out.println("Already at the root directory");
        }
    }

    private void handleDirCommand() {
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

    private void handleMkdirCommand(List<String> args) {
        if (args.size() > 0) {
            String dirName = args.get(0);
            File newDir = new File(currentDir, dirName);
            if (!newDir.exists()) {
                if (newDir.mkdir()) {
                    System.out.println("Directory created successfully.");
                } else {
                    System.out.println("Failed to create directory.");
                }
            } else {
                System.out.println("Directory already exists.");
            }
        } else {
            System.out.println("mkdir requires an argument");
        }
    }

    private void handleRunCommand(List<String> args) {
        if (args.size() > 0) {
            String processName = args.get(0);
            File processFile = new File(currentDir, processName);

            if (!processFile.exists()) {
                System.out.println("Failed to execute program: Cannot find file " + processName);
                return;
            }

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(processFile.getAbsolutePath());
                processBuilder.directory(currentDir); // Postavljanje trenutnog direktorijuma kao radnog direktorijuma
                Process process = processBuilder.start();
                process.waitFor();

                System.out.println("Process " + processName + " has finished.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Failed to execute program: " + e.getMessage());
            }
        } else {
            System.out.println("run requires an argument");
        }
    }

    private void handleTreeCommand(File dir) {
        printTree(dir, 0);
    }

    private void printTree(File dir, int level) {
        if (dir.isDirectory()) {
            System.out.println(getIndent(level) + "+-- " + dir.getName());
            File[] filesList = dir.listFiles();
            if (filesList != null) {
                for (File file : filesList) {
                    printTree(file, level + 1);
                }
            }
        } else {
            System.out.println(getIndent(level) + "+-- " + dir.getName());
        }
    }

    private String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("   ");
        }
        return indent.toString();
    }

    private void handleDelCommand(List<String> args) {
        if (args.size() > 0) {
            String fileName = args.get(0);
            File file = new File(currentDir, fileName);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File/directory deleted successfully.");
                } else {
                    System.out.println("Failed to delete file/directory.");
                }
            } else {
                System.out.println("File/directory does not exist.");
            }
        } else {
            System.out.println("del requires an argument");
        }
    }

    private void handleCdCommand(List<String> args) {
        if (args.size() > 0) {
            String dirName = args.get(0);
            File newDir = new File(currentDir, dirName);
            if (newDir.exists() && newDir.isDirectory()) {
                currentDir = newDir;
                System.out.println("Changed directory to " + currentDir.getAbsolutePath());
            } else {
                System.out.println("Directory does not exist.");
            }
        } else {
            System.out.println("cd requires an argument");
        }
    }

    private void handleLsCommand() {
        File[] filesList = currentDir.listFiles();
        if (filesList != null) {
            for (File file : filesList) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("Unable to list files in the directory.");
        }
    }
}
