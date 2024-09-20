import Assembler.Assembler;
import Commands.CommandExecutor;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class SystemUIController {
    private CommandExecutor commandExecutor;
    @FXML
    private TextArea outputArea;  // Linked with FXML's outputArea
    @FXML
    private TextField inputField; // Linked with FXML's inputField

    @FXML
    public void initialize() {
        FileSystem fileSystem = new FileSystem(outputArea);
        MemoryManager memoryManager = new MemoryManager(12000);
        DiskScheduler diskScheduler = new DiskScheduler(200);
        ProcessScheduler processScheduler = new ProcessScheduler(memoryManager, fileSystem, outputArea);
        Assembler assembler = new Assembler(outputArea);
        processScheduler.setAssembler(assembler);
        assembler.setProcessScheduler(processScheduler, fileSystem);
        this.commandExecutor = new CommandExecutor(fileSystem, processScheduler, memoryManager, diskScheduler, assembler);

        // Add key listener for Enter key
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onExecute();
            }
        });
    }

    @FXML
    private void onExecute() {
        String command = inputField.getText();
        if (command != null && !command.trim().isEmpty()) {
            String output = commandExecutor.executeCommand(command);
            outputArea.appendText("> " + command + "\n");
            outputArea.appendText(output + "\n");
            inputField.clear();
        }
    }
}
