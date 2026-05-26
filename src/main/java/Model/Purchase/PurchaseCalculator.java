package Model.Purchase;

import Model.Share;
import Model.Transaction.TransactionCalculator;
import java.math.BigDecimal;

/**
 * Calculates costs and totals for purchase transactions.
 * A PurchaseCalculator computes the gross cost (quantity × price), applies
 * a commission fee, and produces the total transaction cost.
 */
public class PurchaseCalculator implements TransactionCalculator {
  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;

  /**
   * Constructs a PurchaseCalculator for the given share.
   *
   * @param share the share being purchased. Cannot be null
   */
  public PurchaseCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }

  /**
   * Calculates the gross cost without fees (quantity × purchase price).
   *
   * @return the gross cost
   */
  @Override
  public BigDecimal calculateGross() {
    return this.purchasePrice.multiply(this.quantity);
  }

  /**
   * Calculates the purchase commission (0.5% of gross).
   *
   * @return the commission fee
   */
  @Override
  public BigDecimal calculateCommission() {
    BigDecimal rate = new BigDecimal("0.005");
    return calculateGross().multiply(rate);
  }

  /**
   * Calculates the tax on purchase (always 0 for purchases).
   *
   * @return zero
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO;
  }

  /**
   * Calculates the total purchase cost (gross + commission + tax).
   *
   * @return the total transaction cost
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().add(calculateCommission()).add(calculateTax());
  }
}
