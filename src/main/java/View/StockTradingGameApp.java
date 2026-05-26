package View;

import Controller.StockFileHandler;
import Model.*;
import Model.Exchange.Exchange;
import Model.Player.Player;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Stock Trading Game.
 * Manages the application lifecycle, window setup, scene transitions,
 * and exchanges between the game setup and main game views.
 */
public class StockTradingGameApp extends Application {
  private Stage primaryStage;
  private Exchange exchange;
  private Player player;
  private StockFileHandler fileHandler;

  /**
   * Initializes and displays the primary stage.
   *
   * @param primaryStage the primary window stage provided by JavaFX
   */
  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.fileHandler = new StockFileHandler();
        
    primaryStage.setTitle("Stock Trading Game");
    primaryStage.setWidth(1200);
    primaryStage.setHeight(800);
        
    showGameSetup();
    primaryStage.show();
  }

  /**
   * Displays the game setup scene where players configure their game.
   */
  private void showGameSetup() {
    GameSetupScene setupScene = new GameSetupScene(this::startGame);
    primaryStage.setScene(setupScene.getScene());
  }

  /**
   * Starts the main game scene after setup is complete.
   *
   * @param gameData the game configuration data from the setup scene
   */
  private void startGame(GameSetupScene.StartGameData gameData) {
    this.exchange = gameData.exchange;
    this.player = new Player(gameData.playerName, gameData.startingCapital);
        
    MainGameScene gameScene = new MainGameScene(exchange, player, this::endGame);
    primaryStage.setScene(gameScene.getScene());
  }

  /**
   * Ends the game and closes the application.
   */
  private void endGame() {
    primaryStage.close();
  }

  /**
   * Main method to launch the JavaFX application.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    launch(args);
  }
}
