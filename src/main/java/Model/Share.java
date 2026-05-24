package Model;

import java.math.BigDecimal;

/**
 * Share class.
 */
public class Share {

  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  /**
   * Share method that has stock, quantity and purchaseprice.
   *
   * @param stock stock name
   * @param quantity quantity of stock
   * @param purchasePrice price of stock
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

  public Stock getStock() {
    return stock;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }
    
}
