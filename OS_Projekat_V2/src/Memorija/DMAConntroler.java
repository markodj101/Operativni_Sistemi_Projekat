package Memorija;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DMAConntroler {

    public static void fromDiskToRam(Process process) {
        try {
            process.setInstructions((ArrayList<String>) Files.readAllLines(Paths.get(process.getFilePath())));
        } catch (IOException e) {
            System.err.println("Error reading ASM file: " + e.getMessage());
        }

        int requiredMemory = process.getInstructions().size();
        Disc.allocateMemory(process.getProcessName(), requiredMemory);
        // Load instructions into RAM (omitted for brevity)
    }

    public static void fromRamToDisk(Process process) {
        try {
            String currentDir = System.getProperty("user.dir");  // Assuming current directory for simplicity
            File newFile = new File(currentDir + "\\" + process.getSaveFileName() + ".txt");
            if (!newFile.exists()) {
                newFile.createNewFile();

                FileWriter fw = new FileWriter(newFile);
                String resultMessage = "Execution result: " + process.getRezultat();
                fw.write(resultMessage);
                fw.close();

                ArrayList<String> content = new ArrayList<>();
                content.add(resultMessage);

                // Save to Disc
                Disc.deallocateMemory(process.getProcessName());
                Disc.allocateMemory(process.getSaveFileName(), content.size());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
