package Model.Sale;

import Model.Share;
import Model.Transaction.TransactionCalculator;
import java.math.BigDecimal;

/**
 * Calculates costs, taxes, and proceeds for sale transactions.
 * A SaleCalculator computes the gross revenue (quantity × sales price), applies
 * a commission fee, calculates capital gains tax, and produces the net proceeds.
 */
public class SaleCalculator implements TransactionCalculator {
  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;

  /**
   * Constructs a SaleCalculator for the given share.
   *
   * @param share the share being sold. Cannot be null
   */
  public SaleCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.salesPrice = share.getStock().getSalesPrice(); 
    this.quantity = share.getQuantity();
  }

  /**
   * Calculates the gross revenue (quantity × sales price).
   *
   * @return the gross revenue
   */
  @Override
  public BigDecimal calculateGross() {
    return this.salesPrice.multiply(this.quantity);
  }

  /**
   * Calculates the sale commission (1% of gross).
   *
   * @return the commission fee
   */
  @Override
  public BigDecimal calculateCommission() {
    BigDecimal rate = new BigDecimal("0.01");
    return calculateGross().multiply(rate);
  }

  /**
   * Calculates the capital gains tax (30% of profit).
   * Profit is calculated as: revenue - commission - original purchase cost.
   *
   * @return the capital gains tax
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal sellingCost = this.purchasePrice.multiply(this.quantity);
    BigDecimal profit = calculateGross().subtract(calculateCommission()).subtract(sellingCost);
    BigDecimal rate = new BigDecimal("0.3");
    return profit.multiply(rate);
  }

  /**
   * Calculates the net proceeds (gross - commission - tax).
   *
   * @return the total amount credited to the player
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
    


}
