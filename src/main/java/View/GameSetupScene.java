package View;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Controller.StockFileHandler;
import Model.Exchange;
import Model.Stock;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public class GameSetupScene {
    private Scene scene;
    private Consumer<StartGameData> onGameStart;
    private File selectedFile;
    private Label fileLabel;

    public GameSetupScene(Consumer<StartGameData> onGameStart) {
        this.onGameStart = onGameStart;
        this.scene = createScene();
    }

    private Scene createScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: Arial; -fx-font-size: 12;");

        // title
        Label titleLabel = new Label("Stock Trading Game");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        root.getChildren().add(titleLabel);

        // player name
        HBox nameBox = new HBox(10);
        Label nameLabel = new Label("Player Name:");
        nameLabel.setMinWidth(120);
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // starting capital
        HBox capitalBox = new HBox(10);
        Label capitalLabel = new Label("Starting Capital:");
        capitalLabel.setMinWidth(120);
        TextField capitalField = new TextField();
        capitalField.setPromptText("Enter amount (e.g., 10000)");
        capitalBox.getChildren().addAll(capitalLabel, capitalField);

        // file selection
        HBox fileBox = new HBox(10);
        Label fileSelectLabel = new Label("Stock Data File:");
        fileSelectLabel.setMinWidth(120);
        fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #666666;");
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> selectFile());
        fileBox.getChildren().addAll(fileSelectLabel, fileLabel, browseButton);

        // button box
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 14; -fx-padding: 10 40 10 40;");
        startButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String capitalStr = capitalField.getText().trim();
            
            if (name.isEmpty()) {
                showAlert("Validation Error", "Please enter a player name");
                return;
            }
            
            if (capitalStr.isEmpty()) {
                showAlert("Validation Error", "Please enter starting capital");
                return;
            }
            
            if (selectedFile == null) {
                showAlert("Validation Error", "Please select a stock data file");
                return;
            }
            
            try {
                BigDecimal capital = new BigDecimal(capitalStr);
                if (capital.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert("Validation Error", "Starting capital must be greater than 0");
                    return;
                }
                
                StockFileHandler fileHandler = new StockFileHandler();
                List<Stock> stocks = fileHandler.loadStocksFromFile(selectedFile.getAbsolutePath());
                
                if (stocks.isEmpty()) {
                    showAlert("Error", "No stocks found in the selected file");
                    return;
                }
                
                Exchange exchange = new Exchange("Main Exchange", stocks);
                onGameStart.accept(new StartGameData(name, capital, exchange));
            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Starting capital must be a valid number");
            } catch (Exception ex) {
                showAlert("Error", "Failed to load file: " + ex.getMessage());
            }
        });
        
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));
        buttonBox.getChildren().addAll(startButton, exitButton);

        root.getChildren().addAll(nameBox, capitalBox, fileBox, buttonBox);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        return new Scene(scrollPane, 600, 400);
    }

    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Stock Data File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedFile = file;
            fileLabel.setText(file.getName());
            fileLabel.setStyle("-fx-text-fill: #000000;");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }

    public static class StartGameData {
        public final String playerName;
        public final BigDecimal startingCapital;
        public final Exchange exchange;

        public StartGameData(String playerName, BigDecimal startingCapital, Exchange exchange) {
            this.playerName = playerName;
            this.startingCapital = startingCapital;
            this.exchange = exchange;
        }
    }
}
