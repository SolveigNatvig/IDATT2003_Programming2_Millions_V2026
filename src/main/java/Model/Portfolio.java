package Model;

import Model.Sale.SaleCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a portfolio of shares held by a player.
 * A Portfolio manages a collection of Share objects and provides methods to add,
 * remove, and query holdings. It can calculate the total net worth based on current
 * market prices.
 */
public class Portfolio {

  private final List<Share> shares;

  /**
   * Constructs a new empty Portfolio.
   */
  public Portfolio() {
    this.shares = new ArrayList<>();
  }

  /**
   * Adds a share to the portfolio.
   *
   * @param share the share to add. Cannot be null
   * @return true if the share was added successfully
   * @throws IllegalArgumentException if share is null
   */
  public boolean addShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.add(share);
  }

  /**
   * Removes a share from the portfolio.
   *
   * @param share the share to remove. Cannot be null
   * @return true if the share was removed; false if it was not in the portfolio
   * @throws IllegalArgumentException if share is null
   */
  public boolean removeShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.remove(share);
  }

  /**
   * Returns a defensive copy of all shares in the portfolio.
   *
   * @return a copy of the shares list
   */
  public List<Share> getShares() {
    return new ArrayList<>(shares);
  }

  /**
   * Returns all shares in the portfolio for a specific stock symbol.
   *
   * @param symbol the stock symbol to filter by. Cannot be null or blank
   * @return a list of shares matching the symbol
   * @throws IllegalArgumentException if symbol is null or blank
   */
  public List<Share> getShares(String symbol) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    return shares.stream()
        .filter(share -> share.getStock().getSymbol().equals(symbol))
        .toList();
  }

  /**
   * Checks if the portfolio contains a specific share.
   *
   * @param share the share to check for. Cannot be null
   * @return true if the share is in the portfolio; false otherwise
   * @throws IllegalArgumentException if share is null
   */
  public boolean contains(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.contains(share);
  }

  /**
   * Calculates the total net worth of the portfolio based on current market prices.
   * The net worth is the sum of all shares valued at their current sales prices,
   * minus any transaction costs (commissions and taxes).
   *
   * @return the total net worth of all holdings
   */
  public BigDecimal getNetWorth() {
    return shares.stream()
      .map(share -> new SaleCalculator(share).calculateTotal())
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
    
}
