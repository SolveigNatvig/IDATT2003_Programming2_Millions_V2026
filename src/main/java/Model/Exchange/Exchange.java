package Model.Exchange;

import Model.Player.Player;
import Model.Purchase.Purchase;
import Model.Share;
import Model.Stock;
import Model.Transaction.Transaction;
import Model.Transaction.TransactionFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



/**
 * Represents a stock exchange where players can buy and sell stocks.
 * The Exchange maintains a collection of stocks, advances through weeks,
 * and executes trades. It implements the Observer pattern to notify listeners
 * (typically the UI) whenever its state changes.
 */
public class Exchange {

  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;

  // Registered observers
  private final List<ExchangeObserver> observers = new ArrayList<>();

  /**
   * Constructs a new Exchange with the given name and initial stocks.
   *
   * @param name the exchange name. Cannot be null or blank
   * @param stocks the initial list of stocks. Cannot be null or contain null entries
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public Exchange(String name, List<Stock> stocks) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Exchange name cannot be null or blank");
    }
    if (stocks == null) {
      throw new IllegalArgumentException("Stock list cannot be null");
    }
    
    this.name = name;
    this.week = 1;
    this.stockMap = new HashMap<>();
    this.random = new Random();

    for (Stock stock : stocks) {
      if (stock == null) {
        throw new IllegalArgumentException("Stock list must not contain null entries");
      }
      stockMap.put(stock.getSymbol(), stock);
    }
  }

  // ---- Observer ----

  /**
   * Registers an observer to receive notifications of state changes.
   *
   * @param observer the observer to register. Cannot be null
   * @throws IllegalArgumentException if observer is null
   */
  public void addObserver(ExchangeObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer cannot be null");
    }
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * Unregisters an observer from receiving notifications.
   *
   * @param observer the observer to remove
   */
  public void removeObserver(ExchangeObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers of a state change.
   */
  private void notifyObservers() {
    for (ExchangeObserver observer : observers) {
      observer.onExchangeUpdated(this);
    }
  }

  /**
   * Returns the exchange name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current week number.
   *
   * @return the week
   */
  public int getWeek() {
    return week;
  }

  /**
   * Checks if a stock with the given symbol is available on this exchange.
   *
   * @param symbol the stock symbol. Cannot be null or blank
   * @return true if the stock exists; false otherwise
   * @throws IllegalArgumentException if symbol is invalid
   */
  public boolean hasStock(String symbol) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    return stockMap.containsKey(symbol);
  }

  /**
   * Returns the stock with the given symbol.
   *
   * @param symbol the stock symbol. Cannot be null or blank
   * @return the Stock, or null if not found
   * @throws IllegalArgumentException if symbol is invalid
   */
  public Stock getStock(String symbol) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
      }
    return stockMap.get(symbol);
  }

  /**
   * Searches for stocks matching the search term by symbol or company name.
   *
   * @param searchTerm the search term. Cannot be null
   * @return a list of matching stocks (case-insensitive)
   * @throws IllegalArgumentException if searchTerm is null
   */
  public List<Stock> findStocks(String searchTerm) {
    if (searchTerm == null) {
      throw new IllegalArgumentException("Search term cannot be null");
    }
    List<Stock> result = new ArrayList<>();
    String lowerSearch = searchTerm.toLowerCase();

    for (Stock stock : stockMap.values()) {
      if (stock.getSymbol().toLowerCase().contains(lowerSearch)
          || stock.getCompany().toLowerCase().contains(lowerSearch)) {
        result.add(stock);
      }
    }

    return result;
  }

  /**
   * Executes a buy transaction for the specified stock and quantity.
   *
   * @param symbol the stock symbol. Cannot be null or blank
   * @param quantity the number of shares to buy. Must be greater than zero
   * @param player the player executing the purchase. Cannot be null
   * @return the completed Purchase transaction
   * @throws IllegalArgumentException if any parameter is invalid or stock not found
   * @throws IllegalStateException if player has insufficient funds
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    if (quantity == null) {
      throw new IllegalArgumentException("Quantity cannot be null");
    }
    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
 
    Stock stock = getStock(symbol);
    if (stock == null) {
      throw new IllegalArgumentException("No stock found with symbol: " + symbol);
    }

    // Creates a new share based on current selling price
    Share shareToBuy = new Share(stock, quantity, stock.getSalesPrice());
    Purchase purchase = new Purchase(shareToBuy, this.week);
    purchase.commit(player);

    notifyObservers();

    return purchase;
  }

  /**
   * Executes a full sale transaction for all shares in the given holding.
   *
   * @param share the share to sell completely. Cannot be null
   * @param player the player executing the sale. Cannot be null
   * @return the completed Sale transaction
   * @throws IllegalArgumentException if any parameter is invalid
   * @throws IllegalStateException if player doesn't own the share
   */
  public Transaction sell(Share share, Player player) {
    return sell(share, share.getQuantity(), player);
  }

  /**
   * Executes a partial or complete sale transaction.
   * If the sell quantity is less than the share quantity, creates a partial sale
   * and leaves the remainder in the portfolio.
   *
   * @param originalShare the share to sell from. Cannot be null
   * @param sellQuantity the quantity to sell. Cannot exceed the original quantity
   * @param player the player executing the sale. Cannot be null
   * @return the completed Sale transaction
   * @throws IllegalArgumentException if any parameter is invalid
   * @throws IllegalStateException if sell quantity exceeds holding
   */
  public Transaction sell(Share originalShare, BigDecimal sellQuantity, Player player) {
    if (originalShare == null || sellQuantity == null) {
      throw new IllegalArgumentException("Share and quantity cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    // Cannot sell more shares than you have
    if (sellQuantity.compareTo(originalShare.getQuantity()) > 0) {
      throw new IllegalArgumentException("Cannot sell more shares than owned");
    }


    Share shareToSell;

    if (sellQuantity.compareTo(originalShare.getQuantity()) < 0) {


      // Partial sale:
      // Original quantity - quantity sold = remaining quantity

      player.getPortfolio().removeShare(originalShare);

      BigDecimal remainderShare = originalShare.getQuantity().subtract(sellQuantity);

      player.getPortfolio().addShare(new Share(originalShare
          .getStock(), remainderShare, originalShare.getPurchasePrice()));

      
      // Temporarily adds the subset to the portfolio so that Sale.commit() can find it

      shareToSell = new Share(originalShare
        .getStock(), sellQuantity, originalShare.getPurchasePrice());

      player.getPortfolio().addShare(shareToSell);

    }

    else {
      // Complete sale: the entire share is sold as normal
      shareToSell = originalShare;
    }

    // Salestransaction
    Transaction sale = TransactionFactory.createSale(shareToSell, this.week);

    sale.commit(player);
    notifyObservers();
    return sale;
  }

  /**
   * Advances the game to the next week and updates all stock prices.
   * Each stock's price is randomly adjusted by -10% to +10% per week.
   * Notifies all observers of the change.
   */
  public void advance() {
        
    week++;

    for (Stock stock : stockMap.values()) { // Retrieves the stock-objects

      BigDecimal currentPrice = stock.getSalesPrice(); // Retrieves last price from Stock

      double changePercent = (random.nextDouble() - 0.5) * 0.1; 

      BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent)); 

      BigDecimal newPrice = currentPrice.add(change); 

      if (newPrice.compareTo(BigDecimal.ZERO) > 0) { // avoid negative price
        stock.addNewSalesPrice(newPrice);
      }
    }

    notifyObservers();
  }

  /**
   * Returns the top-performing stocks (gainers) for the current week.
   * Gainers are stocks with the highest positive price change.
   *
   * @param limit the maximum number of gainers to return
   * @return a list of the top gainers, sorted by price change (highest first)
   */
  public List<Stock> getGainers(int limit) { // shows the "winners"
    return stockMap.values().stream()
      .sorted((s1, s2) -> s2.getLatestPriceChange().compareTo(s1.getLatestPriceChange()))
      .limit(limit)
      .toList();
  }

  /**
   * Returns the lowest-performing stocks (losers) for the current week.
   * Losers are stocks with the highest negative price change.
   *
   * @param limit the maximum number of losers to return
   * @return a list of the top losers, sorted by price change (lowest first)
   */
  public List<Stock> getLosers(int limit) { // shows the "losers"
    return stockMap.values().stream()
      .sorted((s1, s2) -> s1.getLatestPriceChange().compareTo(s2.getLatestPriceChange()))
      .limit(limit)
      .toList();
  }
}
