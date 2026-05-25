package Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock traded on the exchange.
 * A Stock tracks the company's trading symbol, company name, and historical price
 * data. Prices are maintained in chronological order, with the latest price being
 * the current sales price.
 */
public class Stock {

  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  /**
   * Constructs a new Stock with the given symbol, company name, and initial sales price.
   *
   * @param symbol the unique ticker symbol (e.g., "AAPL"). Cannot be null or blank
   * @param company the company name. Cannot be null or blank
   * @param salesPrice the initial sales price. Cannot be null or negative
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public Stock(String symbol, String company, BigDecimal salesPrice) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    if (company == null || company.isBlank()) {
      throw new IllegalArgumentException("Company name cannot be null or blank");
    }
    if (salesPrice == null) {
      throw new IllegalArgumentException("Initial sales price cannot be null");
    }
    if (salesPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Initial sales price cannot be negative");
    }

    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  /**
   * Returns the ticker symbol of this stock.
   *
   * @return the stock symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns the company name associated with this stock.
   *
   * @return the company name
   */
  public String getCompany() {
    return company;
  }

  /**
   * Returns the current (most recent) sales price of this stock.
   *
   * @return the current sales price
   */
  public BigDecimal getSalesPrice() {
    return prices.get(prices.size() - 1);
  }

  /**
   * Records a new sales price for this stock.
   *
   * @param price the new sales price. Cannot be null or negative
   * @throws IllegalArgumentException if price is invalid
   */
  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    prices.add(price);
  }

  public List<BigDecimal> getHistoricalPrices() {
    return new ArrayList<>(prices); // returns a copy to protect the list itself
  }

  public BigDecimal getHighestPrice() {
    return prices.stream()
    .reduce(prices.get(0), BigDecimal::max);
  }

  public BigDecimal getLowestPrice() {
    return prices.stream()
    .reduce(prices.get(0), BigDecimal::min);
  }

  /**
   * Method that gets the latest price change.
   *
   * @return returns the price change
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.size() < 2) {
      return BigDecimal.ZERO;
    }

    BigDecimal latest = prices.get(prices.size() - 1);
    BigDecimal previous = prices.get(prices.size() - 2);
    return latest.subtract(previous);
  }
}
