package View;

import Model.*;
import Model.Exchange.Exchange;
import Model.Exchange.ExchangeObserver;
import Model.Player.Player;
import Model.Player.PlayerStatus;
import Model.Purchase.Purchase;
import Model.Sale.Sale;
import Model.Sale.SaleCalculator;
import Model.Transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;


/**
 * The main game user interface scene.
 * Displays the game board with stocks, portfolio, trading interface, and transaction
 * history. Implements ExchangeObserver to automatically refresh UI when the game state changes.
 */
public class MainGameScene implements ExchangeObserver {

  private final Scene scene;
  private final Exchange exchange;
  private final Player player;
  private final Runnable onExit;
  private Label statusLabel;

  // References to UI components that need refreshing
  private TableView<PortfolioRow> portfolioTable;
  private ListView<Share> holdingsList;
  private TableView<HistoryRow> historyTable;
  private ComboBox<Integer> weekFilterCombo;

  /**
   * Constructs the main game scene.
   *
   * @param exchange the Exchange managing the game state. Cannot be null
   * @param player the Player being displayed. Cannot be null
   * @param onExit callback to run when exiting the game. Cannot be null
   */
  public MainGameScene(Exchange exchange, Player player, Runnable onExit) {
    this.exchange = exchange;
    this.player = player;
    this.onExit = onExit;

    // Register this view as an observer of the exchange
    this.exchange.addObserver(this);

    this.scene = createScene();
  }

  /**
   * Callback invoked when the Exchange notifies observers of state changes.
   *
   * @param exchange the Exchange that changed
   */
  @Override
  public void onExchangeUpdated(Exchange exchange) {
    updateStatus();
    refreshAllUI();
  }

  /**
   * Creates the main game scene layout.
   *
   * @return the constructed Scene
   */
  private Scene createScene() {
    VBox root = new VBox(0);

    // TOP STATUS BAR
    HBox topBar = new HBox(12);
    topBar.getStyleClass().add("top-bar");
    topBar.setAlignment(Pos.CENTER_LEFT);

    statusLabel = new Label();
    statusLabel.getStyleClass().add("status-label");
    updateStatus();

    Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
    sep.setPrefHeight(20);

    Button nextWeekBtn = new Button("Next week");
    nextWeekBtn.getStyleClass().addAll("action-button");
    nextWeekBtn.setOnAction(e -> exchange.advance()); // observer handles the UI update

    Button exitBtn = new Button("Exit");
    exitBtn.getStyleClass().add("exit-button");
    exitBtn.setOnAction(e -> {
      if (confirm("Exit Game?", "Final Net Worth: $" + formatMoney(getNetWorth()))) {
        exchange.removeObserver(this); // clean up before closing
        onExit.run();
      }
    });

    topBar.getChildren().addAll(statusLabel, sep, nextWeekBtn, exitBtn);

    // MAIN CONTENT TABS
    TabPane tabs = new TabPane();
    tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    tabs.getTabs().addAll(
        new Tab("Stocks", createStocksPanel()),
        new Tab("Portfolio", createPortfolioPanel()),
        new Tab("Trade", createTradePanel()),
        new Tab("History", createHistoryPanel())
    );

    VBox.setVgrow(tabs, Priority.ALWAYS);
    root.getChildren().addAll(topBar, tabs);

    Scene scene = new Scene(root, 1000, 700);
    scene.getStylesheets().add(
        getClass().getResource("/Style/Global.css").toExternalForm()
    );
    return scene;
  }

  /**
   * Creates the stocks panel displaying available stocks.
   *
   * @return the stocks panel
   */
  private VBox createStocksPanel() {
    VBox panel = new VBox(10);
    panel.getStyleClass().add("content-area");

    HBox searchBox = new HBox(8);
    searchBox.setAlignment(Pos.CENTER_LEFT);

    TextField search = new TextField();
    search.setPromptText("Search symbol or company...");
    HBox.setHgrow(search, Priority.ALWAYS);

    Button searchBtn = new Button("Search");
    searchBtn.getStyleClass().add("action-button");

    ComboBox<String> filter = new ComboBox<>();
    filter.setItems(FXCollections.observableArrayList("All", "Gainers", "Losers"));
    filter.setValue("All");

    TableView<StockRow> table = new TableView<>();
    addStockColumns(table);

    Runnable loadStocks = () -> {
      List<Stock> stocks;
      String filterVal = filter.getValue();
      String searchVal = search.getText().trim();

      if (null == filterVal) {
        stocks = exchange.findStocks(searchVal);
      } else {
        stocks = switch (filterVal) {
          case "Gainers" -> exchange.getGainers(20);
          case "Losers" -> exchange.getLosers(20);
          default -> exchange.findStocks(searchVal);
        };
      }

      ObservableList<StockRow> data = FXCollections.observableArrayList();
      for (Stock s : stocks) {
        data.add(new StockRow(s));
      }
      table.setItems(data);
    };

    searchBtn.setOnAction(e -> loadStocks.run());
    filter.setOnAction(e -> loadStocks.run());
    search.setOnAction(e -> loadStocks.run());

    searchBox.getChildren().addAll(search, searchBtn, filter);
    loadStocks.run();

    VBox.setVgrow(table, Priority.ALWAYS);
    panel.getChildren().addAll(searchBox, table);
    return panel;
  }

  /**
   * Creates the portfolio panel showing held shares and net worth.
   *
   * @return the portfolio panel
   */
  private VBox createPortfolioPanel() {
    VBox panel = new VBox(10);
    panel.getStyleClass().add("content-area");

    Label heading = new Label("Holdings:");

    portfolioTable = new TableView<>();
    addPortfolioColumns(portfolioTable);
    updatePortfolio(portfolioTable);

    Button refresh = new Button("Refresh");
    refresh.getStyleClass().add("action-button");
    refresh.setOnAction(e -> updatePortfolio(portfolioTable));

    Button sellSelected = new Button("Sell Selected");
    sellSelected.getStyleClass().add("action-button");
    sellSelected.setOnAction(e -> {
      PortfolioRow selected = portfolioTable.getSelectionModel().getSelectedItem();
      if (selected == null) {
        alert("Error", "Select a holding to sell.");
        return;
      }
      Transaction trans = exchange.sell(selected.s, player); // observer fires refresh
      if (trans != null && trans.isCommitted()) {
        showConfirmation("Sale successful", trans);
      } else {
        alert("Failed", "Could not complete the sale.");
      }
    });

    HBox buttons = new HBox(10, refresh, sellSelected);
    buttons.setAlignment(Pos.CENTER_LEFT);

    VBox.setVgrow(portfolioTable, Priority.ALWAYS);
    panel.getChildren().addAll(heading, portfolioTable, buttons);
    return panel;
  }

  /**
   * Creates the trading panel with buy and sell tabs.
   *
   * @return the trading panel
   */
  private VBox createTradePanel() {
    VBox panel = new VBox(0);
    panel.getStyleClass().add("content-area");

    TabPane tradeTabs = new TabPane();
    tradeTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    tradeTabs.getTabs().addAll(
        new Tab("Buy", createBuyTab()),
        new Tab("Sell", createSellTab())
    );

    VBox.setVgrow(tradeTabs, Priority.ALWAYS);
    panel.getChildren().add(tradeTabs);
    return panel;
  }

  /**
   * Creates the buy transaction tab.
   *
   * @return the buy tab content
   */
  private VBox createBuyTab() {
    VBox box = new VBox(10);
    box.getStyleClass().add("content-area");

    Label heading = new Label("Buy Stocks:");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);

    TextField stockField = new TextField();
    stockField.setPromptText("Stock symbol (e.g. AAPL)");

    TextField qtyField = new TextField();
    qtyField.setPromptText("Quantity");

    form.add(new Label("Symbol:"), 0, 0);
    form.add(stockField, 1, 0);
    form.add(new Label("Quantity:"), 0, 1);
    form.add(qtyField, 1, 1);

    Label infoLabel = new Label();

    stockField.textProperty()
        .addListener((obs, old, val) -> updateBuyInfo(val, qtyField, infoLabel));
    qtyField.textProperty()
        .addListener((obs, old, val) -> updateBuyInfo(stockField.getText(), qtyField, infoLabel));

    Button buyBtn = new Button("Buy");
    buyBtn.getStyleClass().add("action-button");
    buyBtn.setOnAction(e -> {
      try {
        Stock s = exchange.getStock(stockField.getText().toUpperCase());
        if (s == null) {
          alert("Error", "Stock not found");
          return;
        }
        BigDecimal qty = new BigDecimal(qtyField.getText());
        Transaction trans = exchange.buy(s.getSymbol(), qty, player); // observer fires refresh
        if (trans != null && trans.isCommitted()) {
          showConfirmation("Purchase successful", trans);
          stockField.clear();
          qtyField.clear();
          infoLabel.setText("");
        } else {
          alert("Failed", "Insufficient funds or error");
        }
      } catch (Exception ex) {
        alert("Error", ex.getMessage());
      }
    });

    box.getChildren().addAll(heading, form, infoLabel, buyBtn);
    return box;
  }

  /**
   * Creates the sell transaction tab.
   *
   * @return the sell tab content
   */
  private VBox createSellTab() {
    VBox box = new VBox(10);
    box.getStyleClass().add("content-area");

    Label heading = new Label("Your Holdings:");

    holdingsList = new ListView<>();
    holdingsList.setPrefHeight(400);

    holdingsList.setCellFactory(lv -> new ListCell<Share>() {
        @Override
        protected void updateItem(Share s, boolean empty) {
          super.updateItem(s, empty);
          if (empty || s == null) {
            setText(null);
        } else {
            setText(
                s.getStock().getSymbol() + " - " 
                + s.getQuantity() + " @ $" 
                + formatMoney(s.getStock().getSalesPrice())
              );
            }
        }
    });

    updateHoldingsList(holdingsList);

    // Numer of rows
    HBox quantityRow = new HBox(8);
    quantityRow.setAlignment(Pos.CENTER_LEFT);
    TextField quantityField = new TextField();
    quantityField.setPromptText("Antall");
    quantityField.setPrefWidth(120);
    quantityRow.getChildren().addAll(new Label("Quantity to sell:"), quantityField);

    // Fill in the maximum number automatically when selecting a holding
    holdingsList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
      if (selected != null) {
        quantityField.setText(selected.getQuantity().toPlainString());
      }
    });

    Button sellBtn = new Button("Sell Selected");
    sellBtn.setOnAction(e -> {
      Share selected = holdingsList.getSelectionModel().getSelectedItem();
      if (selected == null) {
        alert("Error", "Select a holding to sell");
        return;
      }
            
      try {
        BigDecimal quantity = new BigDecimal(quantityField.getText().trim());
                
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
          alert("Error", "Quantity must be greater than zero");
          return;
        }
                
        Transaction trans = exchange.sell(selected, quantity, player); // observer fires refresh
                
        if (trans != null && trans.isCommitted()) {
          showConfirmation("Sale successful", trans); 
        } else {
          alert("Failed", "Could not complete sale. Check quantity.");
        }
      } catch (NumberFormatException ex) {
        alert("Error", "Enter a valid number for quantity");
      }
    });

    box.getChildren().addAll(heading, holdingsList, quantityRow, sellBtn);

    return box;
  }

  /**
   * Creates the transaction history panel.
   *
   * @return the history panel
   */
  private VBox createHistoryPanel() {
    VBox panel = new VBox(10);
    panel.getStyleClass().add("content-area");

    weekFilterCombo = new ComboBox<>();
    updateWeekCombo(weekFilterCombo);

    historyTable = new TableView<>();
    addHistoryColumns(historyTable);
    updateHistory(historyTable, null);

    weekFilterCombo.setOnAction(e -> updateHistory(
        historyTable,
        weekFilterCombo.getValue() == null || weekFilterCombo.getValue() == 0
        ? null : weekFilterCombo.getValue()
    ));

    HBox filterRow = new HBox(8);
    filterRow.setAlignment(Pos.CENTER_LEFT);
    filterRow.getChildren().addAll(new Label("Week:"), weekFilterCombo);

    VBox.setVgrow(historyTable, Priority.ALWAYS);
    panel.getChildren().addAll(filterRow, historyTable);
    return panel;
  }

  // ---- Column helpers ----

  @SuppressWarnings("unchecked")
private void addStockColumns(TableView<StockRow> table) {
    table.getColumns().addAll(
        createCol("Symbol", 90, "symbol"),
        createCol("Company", 180, "company"),
        createCol("Price", 90, "price"),
        createCol("Change", 90, "change"),
        createCol("High", 90, "high"),
        createCol("Low", 90, "low")
    );
  }

  @SuppressWarnings("unchecked")
  private void addPortfolioColumns(TableView<PortfolioRow> table) {
    table.getColumns().addAll(
        createCol("Symbol", 90, "symbol"),
        createCol("Qty", 70, "qty"),
        createCol("Buy Price", 100, "buyPrice"),
        createCol("Current Price", 120, "currentPrice"),
        createCol("Gain/Loss", 100, "gainLoss")
    );
  }

  @SuppressWarnings("unchecked")
  private void addHistoryColumns(TableView<HistoryRow> table) {
    table.getColumns().addAll(
        createCol("Week", 70, "week"),
        createCol("Type", 70, "type"),
        createCol("Symbol", 90, "symbol"),
        createCol("Qty", 70, "qty"),
        createCol("Total", 110, "total")
    );
  }

  private <T> TableColumn<T, String> createCol(String header, int width, String property) {
    TableColumn<T, String> col = new TableColumn<>(header);
    col.setPrefWidth(width);
    col.setCellValueFactory(cellData -> {
      try {
        return new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getClass()
            .getMethod("get" + capitalize(property))
            .invoke(cellData.getValue()).toString()
        );
      } catch (Exception e) {
        return new javafx.beans.property.SimpleStringProperty("");
      }
    });
    return col;
  }

  private String capitalize(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  // ---- Data updaters ----

  private void updatePortfolio(TableView<PortfolioRow> table) {
    ObservableList<PortfolioRow> data = FXCollections.observableArrayList();
    for (Share s : player.getPortfolio().getShares()) {
      data.add(new PortfolioRow(s));
    }
    table.setItems(data);
  }

  private void updateHoldingsList(ListView<Share> list) {
    ObservableList<Share> items = FXCollections.observableArrayList();
    items.addAll(player.getPortfolio().getShares());
    list.setItems(items);
  }

  private void updateHistory(TableView<HistoryRow> table, Integer week) {
    ObservableList<HistoryRow> data = FXCollections.observableArrayList();
    List<Transaction> trans = week == null
        ? player.getTransactionArchive().getAllTransactions()
        : player.getTransactionArchive().getTransactions(week);
    for (Transaction t : trans) {
      data.add(new HistoryRow(t));
    }
    table.setItems(data);
  }

  private void updateWeekCombo(ComboBox<Integer> combo) {
    ObservableList<Integer> weeks = FXCollections.observableArrayList();
    weeks.add(0);
    int total = player.getTransactionArchive().countDistinctWeeks();
    for (int i = 1; i <= total; i++) {
      weeks.add(i);
    }
    combo.setItems(weeks);
    combo.setValue(0);
  }

  private void updateBuyInfo(String symbol, TextField qtyField, Label label) {
    try {
      if (symbol == null || symbol.isEmpty()) {
        return;
      }
      Stock s = exchange.getStock(symbol.toUpperCase());
      if (s == null) {
        return;
      }
      BigDecimal qty = new BigDecimal(qtyField.getText());
      BigDecimal gross = s.getSalesPrice().multiply(qty);
      BigDecimal comm = gross.multiply(new BigDecimal("0.005"));
      label.setText("Gross: $" + formatMoney(gross) +
            " | Commission: $" + formatMoney(comm) +
            " | Total: $" + formatMoney(gross.add(comm)));
    } catch (Exception e) {
      // ignore partial input
    }
  }

  private void updateStatus() {
    PlayerStatus playerStatus = player.getStatus();
    statusLabel.setText(
        "Player: " + player.getName()
        +  " | Status: " + playerStatus.getDisplayName()
        +  " | Week: " + exchange.getWeek()
        +  " | Cash: $" + formatMoney(player.getMoney()) 
        +  " | Net Worth: $" + formatMoney(getNetWorth())
    );
  }

  private void refreshAllUI() {
    if (portfolioTable != null) {
      updatePortfolio(portfolioTable);
    }
    if (holdingsList != null) {
      updateHoldingsList(holdingsList);
    }
    if (historyTable != null) {
      updateHistory(historyTable,
          weekFilterCombo != null && weekFilterCombo.getValue() != null
          ? weekFilterCombo.getValue() : null);
      if (weekFilterCombo != null) {
        updateWeekCombo(weekFilterCombo);
      }
    }
  }

  private BigDecimal getNetWorth() {
    return player.getMoney().add(player.getPortfolio().getNetWorth());
  }

  private void showConfirmation(String title, Transaction t) {
    StringBuilder msg = new StringBuilder();
    if (t instanceof Purchase) {
      Purchase p = (Purchase) t;
      msg.append("Bought ").append(formatMoney(p.getShare().getQuantity()))
          .append(" @ $").append(formatMoney(p.getShare().getPurchasePrice()))
          .append("\nCost: $").append(formatMoney(p.getCalculator().calculateTotal()));
    } else {
      Sale s = (Sale) t;
      msg.append("Sold ").append(formatMoney(s.getShare().getQuantity()))
          .append(" @ $").append(formatMoney(s.getShare().getStock().getSalesPrice()))
          .append("\nProceeds: $").append(formatMoney(s.getCalculator().calculateTotal()));
    }
    alert(title, msg.toString());
  }

  private void alert(String title, String msg) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setTitle(title);
    a.setHeaderText(null);
    a.setContentText(msg);
    a.showAndWait();
  }

  private boolean confirm(String title, String msg) {
    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
    a.setTitle(title);
    a.setHeaderText(null);
    a.setContentText(msg);
    return a.showAndWait().get() == ButtonType.OK;
  }

  private String formatMoney(BigDecimal n) {
    return n.setScale(2, java.math.RoundingMode.HALF_UP).toString();
  }

  public Scene getScene() {
    return scene;
  }

  // ---- Data row classes ----

  static class StockRow {
    Stock s;

    StockRow(Stock s) {
      this.s = s; 
    }

    public String getSymbol() { 
      return s.getSymbol(); 
    }

    public String getCompany() {
      return s.getCompany(); 
    }

    public String getPrice() {
      return "$" + s.getSalesPrice(); 
    }

    public String getChange() {
      return "$" + s.getLatestPriceChange(); 
    }
    
    public String getHigh() {
      return "$" + s.getHighestPrice(); 
    }

    public String getLow() { 
      return "$" + s.getLowestPrice(); 
    }

  }

  static class PortfolioRow {
    Share s;

    PortfolioRow(Share s) {
      this.s = s; 
    }
    
    public String getSymbol() { 
      return s.getStock().getSymbol(); 
    }

    public String getQty() { 
      return s.getQuantity().toString(); 
    }

    public String getBuyPrice() { 
      return "$" + s.getPurchasePrice(); 
    }

    public String getCurrentPrice() { 
      return "$" + s.getStock().getSalesPrice(); 
    }

    public String getGainLoss() {
      SaleCalculator calc = new SaleCalculator(s);
      BigDecimal val = calc.calculateTotal();
      BigDecimal cost = s.getPurchasePrice().multiply(s.getQuantity());
      return "$" + val.subtract(cost);
    }
  }

  static class HistoryRow {
    Transaction t;

    HistoryRow(Transaction t) { 
      this.t = t; 
    }

    public String getWeek() { 
      return String.valueOf(t.getWeek()); 
    }

    public String getType() { 
      return t instanceof Purchase ? "BUY" : "SELL"; 
    }

    public String getSymbol() { 
      return t.getShare().getStock().getSymbol(); 
    }

    public String getQty() { 
      return t.getShare().getQuantity().toString(); 
    }

    public String getTotal() { 
      return "$" + t.getCalculator().calculateTotal(); 
    }
  }
}
