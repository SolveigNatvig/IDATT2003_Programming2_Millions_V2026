package View;

import Controller.StockFileHandler;
import Model.Exchange;
import Model.Stock;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



/**
 * Displays the game setup scene where players configure their game parameters.
 * Allows players to enter their name, starting capital, and select a stock data file
 * before starting the main game. Notifies listeners when the game starts.
 */
public class GameSetupScene {

  private final Scene scene;
  private final Consumer<StartGameData> onGameStart;
  private File selectedFile;
  private Label fileLabel;

  /**
   * Constructs a GameSetupScene with a callback for when the game starts.
   *
   * @param onGameStart callback invoked when the player starts the game. Cannot be null
   */
  public GameSetupScene(Consumer<StartGameData> onGameStart) {
    this.onGameStart = onGameStart;
    this.scene = createScene();
  }

  /**
   * Creates the UI layout for the setup scene.
   *
   * @return the constructed Scene
   */
  private Scene createScene() {

    // Card (centered cream box)
    VBox card = new VBox(20);
    card.getStyleClass().add("card");
    card.setMaxWidth(560);
    card.setMinWidth(400);

    // Title
    Label titleLabel = new Label("Stock Trading Game");
    titleLabel.getStyleClass().add("title-label");

    // Spacer below title
    Region titleSpacer = new Region();
    titleSpacer.setMinHeight(6);

    // Player name row
    HBox nameBox = new HBox(16);
    nameBox.setAlignment(Pos.CENTER_LEFT);
    nameBox.setMaxWidth(Double.MAX_VALUE);

    Label nameLabel = new Label("Player:");
    nameLabel.setMinWidth(130);
    nameLabel.getStyleClass().add("form-label");

    TextField nameField = new TextField();
    nameField.setPromptText("Enter your name");
    HBox.setHgrow(nameField, Priority.ALWAYS);

    nameBox.getChildren().addAll(nameLabel, nameField);

    // Starting capital row
    HBox capitalBox = new HBox(16);
    capitalBox.setAlignment(Pos.CENTER_LEFT);
    capitalBox.setMaxWidth(Double.MAX_VALUE);

    Label capitalLabel = new Label("Start Capital:");
    capitalLabel.setMinWidth(130);
    capitalLabel.getStyleClass().add("form-label");

    TextField capitalField = new TextField();
    capitalField.setPromptText("e.g. 10000");
    HBox.setHgrow(capitalField, Priority.ALWAYS);

    capitalBox.getChildren().addAll(capitalLabel, capitalField);

    // File selection row
    HBox fileBox = new HBox(16);
    fileBox.setAlignment(Pos.CENTER_LEFT);
    fileBox.setMaxWidth(Double.MAX_VALUE);

    Label fileSelectLabel = new Label("Stock Data File:");
    fileSelectLabel.setMinWidth(130);
    fileSelectLabel.getStyleClass().add("form-label");

    fileLabel = new Label("No file selected");
    fileLabel.getStyleClass().add("file-label");
    HBox.setHgrow(fileLabel, Priority.ALWAYS);

    Button browseButton = new Button("Browse");
    browseButton.getStyleClass().add("browse-button");
    browseButton.setOnAction(e -> selectFile());

    fileBox.getChildren().addAll(fileSelectLabel, fileLabel, browseButton);

    // Spacer before buttons
    Region buttonSpacer = new Region();
    buttonSpacer.setMinHeight(10);

    // Button row 
    HBox buttonBox = new HBox(12);
    buttonBox.setAlignment(Pos.CENTER_LEFT);
    buttonBox.setMaxWidth(Double.MAX_VALUE);

    Button startButton = new Button("Start Game");
    startButton.getStyleClass().add("start-button");
    startButton.setMaxWidth(Double.MAX_VALUE);
    startButton.setOnAction(e -> handleStart(nameField, capitalField));
    HBox.setHgrow(startButton, Priority.ALWAYS);

    Button exitButton = new Button("Exit");
    exitButton.getStyleClass().add("exit-button");
    exitButton.setMaxWidth(Double.MAX_VALUE);
    exitButton.setOnAction(e -> System.exit(0));
    HBox.setHgrow(exitButton, Priority.ALWAYS);

    buttonBox.getChildren().addAll(startButton, exitButton);

    card.getChildren().addAll(
        titleLabel,
        titleSpacer,
        nameBox,
        capitalBox,
        fileBox,
        buttonSpacer,
        buttonBox
    );

    // Centers the root
    VBox root = new VBox(card);
    root.setAlignment(Pos.CENTER);
    root.setFillWidth(false);

    Scene scene = new Scene(root, 800, 550);
    scene.getStylesheets().add(
        getClass().getResource("/Style/Global.css").toExternalForm()
    );

    return scene;
  }

  /**
   * Validates inputs and starts the game when the Start button is clicked.
   *
   * @param nameField the text field containing the player name
   * @param capitalField the text field containing the starting capital
   */
  private void handleStart(TextField nameField, TextField capitalField) {
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
  }

  /**
   * Opens a file chooser dialog to select a stock data file.
   */
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
    }    
  }

  /**
   * Displays an alert dialog with the given title and message.
   *
   * @param title the dialog title
   * @param message the alert message
   */
  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Returns the Scene for display in the application window.
   *
   * @return the setup scene
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Data class encapsulating game setup parameters.
   * Contains the exchange configured with stocks, player name, and starting capital.
   */
  public static class StartGameData {
    public final String playerName;
    public final BigDecimal startingCapital;
    public final Exchange exchange;

    /**
     * StartGameData method that includes playername, startingcapital and exchange.
     *
     * @param playerName the player's name
     * @param startingCapital startingcapital of the player
     * @param exchange exchange class
     */
    public StartGameData(String playerName, BigDecimal startingCapital, Exchange exchange) {
      this.playerName = playerName;
      this.startingCapital = startingCapital;
      this.exchange = exchange;
    }
  }
}
