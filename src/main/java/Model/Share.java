package Model;

import java.math.BigDecimal;

/**
 * Represents a share holding of a stock.
 * A Share encapsulates a quantity of a stock purchased at a specific price.
 * It provides information about the stock and the purchase terms.
 */
public class Share {

  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  /**
   * Constructs a new Share with the specified stock, quantity, and purchase price.
   *
   * @param stock the Stock being held. Cannot be null
   * @param quantity the number of shares held. Must be greater than zero
   * @param purchasePrice the price per share at purchase. Cannot be null or negative
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    if (stock == null) {
      throw new IllegalArgumentException("Stock cannot be null");
    }      
    if (quantity == null) {
      throw new IllegalArgumentException("Quantity cannot be null");
    }      
    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (purchasePrice == null) {
      throw new IllegalArgumentException("Purchase price cannot be null");
    }
    if (purchasePrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Purchase price cannot be negative");
    }

    this.stock = stock;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

  /**
   * Returns the Stock associated with this share.
   *
   * @return the stock being held
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the quantity of shares held.
   *
   * @return the number of shares
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   * Returns the price per share at the time of purchase.
   *
   * @return the purchase price
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }
    
}
