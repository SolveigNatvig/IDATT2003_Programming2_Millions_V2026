package View;

import Controller.StockFileHandler;
import Model.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * StockTradingGameApp class.
 */
public class StockTradingGameApp extends Application {
  private Stage primaryStage;
  private Exchange exchange;
  private Player player;
  private StockFileHandler fileHandler;

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

  private void showGameSetup() {
    GameSetupScene setupScene = new GameSetupScene(this::startGame);
    primaryStage.setScene(setupScene.getScene());
  }

  private void startGame(GameSetupScene.StartGameData gameData) {
    this.exchange = gameData.exchange;
    this.player = new Player(gameData.playerName, gameData.startingCapital);
        
    MainGameScene gameScene = new MainGameScene(exchange, player, this::endGame);
    primaryStage.setScene(gameScene.getScene());
  }

  private void endGame() {
    primaryStage.close();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
