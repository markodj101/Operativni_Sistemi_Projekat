import Assembler.Assembler;
import Commands.CommandExecutor;
import Disk.DiskScheduler;
import FileSistem.FileSystem;
import Memorija.MemoryManager;
import Rasporedjivanje.ProcessScheduler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SystemUI extends Application {
    private CommandExecutor commandExecutor;
    private TextArea outputArea;
    private TextField inputField;

    @Override
    public void start(Stage primaryStage) {
        // Initialize components
        FileSystem fileSystem = new FileSystem();
        MemoryManager memoryManager = new MemoryManager(12000);
        DiskScheduler diskScheduler = new DiskScheduler(200);
        ProcessScheduler processScheduler = new ProcessScheduler(memoryManager, fileSystem);
        Assembler assembler = new Assembler();
        processScheduler.setAssembler(assembler);
        assembler.setProcessScheduler(processScheduler, fileSystem);
        this.commandExecutor = new CommandExecutor(fileSystem, processScheduler, memoryManager, diskScheduler, assembler);

        // Create the UI components
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.getStyleClass().add("output-area"); // Apply CSS class

        inputField = new TextField();
        inputField.getStyleClass().add("input-field"); // Apply CSS class

        Button executeButton = new Button("Execute");
        executeButton.getStyleClass().add("execute-button"); // Apply CSS class

        // Set up button actions
        executeButton.setOnAction(e -> {
            String command = inputField.getText();
            executeCommand(command);
            inputField.clear();
        });

        inputField.setOnAction(e -> {
            String command = inputField.getText();
            executeCommand(command);
            inputField.clear();
        });

        // Arrange components in the layout
        BorderPane root = new BorderPane();
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(outputArea, inputField, executeButton);
        root.setCenter(vbox);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // Load CSS file
        primaryStage.setScene(scene);
        primaryStage.setTitle("System Terminal");
        primaryStage.show();

        // Focus on the input field when the application starts
        inputField.requestFocus();
    }

    private void executeCommand(String command) {
        String output = commandExecutor.executeCommand(command);
        outputArea.appendText("> " + command + "\n");
        outputArea.appendText(output + "\n");
        // Scroll to the bottom of the outputArea
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
