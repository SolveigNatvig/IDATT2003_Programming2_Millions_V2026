package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Model.*;

import java.math.BigDecimal;
import java.util.List;

public class MainGameScene {
    private Scene scene;
    private Exchange exchange;
    private Player player;
    private Runnable onExit;
    private Label statusLabel;

    public MainGameScene(Exchange exchange, Player player, Runnable onExit) {
        this.exchange = exchange;
        this.player = player;
        this.onExit = onExit;
        this.scene = createScene();
    }

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
        nextWeekBtn.setOnAction(e -> advance());

        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("exit-button");
        exitBtn.setOnAction(e -> {
            if (confirm("Exit Game?", "Final Net Worth: $" + formatMoney(getNetWorth()))) {
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

            if ("Gainers".equals(filterVal)) {
                stocks = exchange.getGainers(20);
            } else if ("Losers".equals(filterVal)) {
                stocks = exchange.getLosers(20);
            } else {
                stocks = exchange.findStocks(searchVal);
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

    private VBox createPortfolioPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("content-area");

        Label heading = new Label("Holdings:");

        TableView<PortfolioRow> table = new TableView<>();
        addPortfolioColumns(table);
        updatePortfolio(table);

        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("action-button");
        refresh.setOnAction(e -> updatePortfolio(table));

        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, table, refresh);
        return panel;
    }

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

        stockField.textProperty().addListener((obs, old, val) -> updateBuyInfo(val, qtyField, infoLabel));
        qtyField.textProperty().addListener((obs, old, val) -> updateBuyInfo(stockField.getText(), qtyField, infoLabel));

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
                Transaction trans = exchange.buy(s.getSymbol(), qty, player);
                if (trans != null && trans.isCommitted()) {
                    showConfirmation("Purchase successful", trans);
                    stockField.clear();
                    qtyField.clear();
                    infoLabel.setText("");
                    updateStatus();
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

    private VBox createSellTab() {
        VBox box = new VBox(10);
        box.getStyleClass().add("content-area");

        Label heading = new Label("Your Holdings:");

        ListView<String> holdingsList = new ListView<>();
        holdingsList.setPrefHeight(200);
        VBox.setVgrow(holdingsList, Priority.ALWAYS);
        updateHoldingsList(holdingsList);

        Label infoLabel = new Label();

        Button sellBtn = new Button("Sell Selected");
        sellBtn.getStyleClass().add("action-button");
        sellBtn.setOnAction(e -> {
            String selected = holdingsList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                alert("Error", "Select a holding to sell");
                return;
            }
            try {
                String symbol = selected.split(" ")[0];
                List<Share> shares = player.getPortfolio().getShares(symbol);
                if (shares.isEmpty()) return;

                Transaction trans = exchange.sell(shares.get(0), player);
                if (trans != null && trans.isCommitted()) {
                    showConfirmation("Sale successful", trans);
                    updateHoldingsList(holdingsList);
                    updateStatus();
                }
            } catch (Exception ex) {
                alert("Error", ex.getMessage());
            }
        });

        box.getChildren().addAll(heading, holdingsList, infoLabel, sellBtn);
        return box;
    }

    private VBox createHistoryPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("content-area");

        ComboBox<Integer> weekFilter = new ComboBox<>();
        updateWeekCombo(weekFilter);

        TableView<HistoryRow> table = new TableView<>();
        addHistoryColumns(table);
        updateHistory(table, null);

        weekFilter.setOnAction(e -> updateHistory(
            table,
            weekFilter.getValue() == null || weekFilter.getValue() == 0 ? null : weekFilter.getValue()
        ));

        HBox filterRow = new HBox(8);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.getChildren().addAll(new Label("Week:"), weekFilter);

        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(filterRow, table);
        return panel;
    }

    // ---- Column helpers ----

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

    private void addPortfolioColumns(TableView<PortfolioRow> table) {
        table.getColumns().addAll(
            createCol("Symbol", 90, "symbol"),
            createCol("Qty", 70, "qty"),
            createCol("Buy Price", 100, "buyPrice"),
            createCol("Current Price", 120, "currentPrice"),
            createCol("Gain/Loss", 100, "gainLoss")
        );
    }

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

    private void updateHoldingsList(ListView<String> list) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Share s : player.getPortfolio().getShares()) {
            items.add(s.getStock().getSymbol() + " - " +
                formatMoney(s.getQuantity()) + " @ $" +
                formatMoney(s.getStock().getSalesPrice()));
        }
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
        for (int i = 1; i <= total; i++) weeks.add(i);
        combo.setItems(weeks);
        combo.setValue(0);
    }

    private void updateBuyInfo(String symbol, TextField qtyField, Label label) {
        try {
            if (symbol == null || symbol.isEmpty()) return;
            Stock s = exchange.getStock(symbol.toUpperCase());
            if (s == null) return;
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
        statusLabel.setText(
            "Player: " + player.getName() +
            " | Week: " + exchange.getWeek() +
            " | Cash: $" + formatMoney(player.getMoney()) +
            " | Net Worth: $" + formatMoney(getNetWorth())
        );
    }

    private void advance() {
        exchange.advance();
        updateStatus();
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
        StockRow(Stock s) { this.s = s; }
        public String getSymbol() { return s.getSymbol(); }
        public String getCompany() { return s.getCompany(); }
        public String getPrice() { return "$" + s.getSalesPrice(); }
        public String getChange() { return "$" + s.getLatestPriceChange(); }
        public String getHigh() { return "$" + s.getHighestPrice(); }
        public String getLow() { return "$" + s.getLowestPrice(); }
    }

    static class PortfolioRow {
        Share s;
        PortfolioRow(Share s) { this.s = s; }
        public String getSymbol() { return s.getStock().getSymbol(); }
        public String getQty() { return s.getQuantity().toString(); }
        public String getBuyPrice() { return "$" + s.getPurchasePrice(); }
        public String getCurrentPrice() { return "$" + s.getStock().getSalesPrice(); }
        public String getGainLoss() {
            SaleCalculator calc = new SaleCalculator(s);
            BigDecimal val = calc.calculateTotal();
            BigDecimal cost = s.getPurchasePrice().multiply(s.getQuantity());
            return "$" + val.subtract(cost);
        }
    }

    static class HistoryRow {
        Transaction t;
        HistoryRow(Transaction t) { this.t = t; }
        public String getWeek() { return String.valueOf(t.getWeek()); }
        public String getType() { return t instanceof Purchase ? "BUY" : "SELL"; }
        public String getSymbol() { return t.getShare().getStock().getSymbol(); }
        public String getQty() { return t.getShare().getQuantity().toString(); }
        public String getTotal() { return "$" + t.getCalculator().calculateTotal(); }
    }
}
