package Model;

import java.math.BigDecimal;

/**
 * PurchaseCalculator class that does calculations.
 */
public class PurchaseCalculator implements TransactionCalculator {
  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;
    
  public PurchaseCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }

  @Override
  public BigDecimal calculateGross() {
    return this.purchasePrice.multiply(this.quantity);
  }

  @Override
  public BigDecimal calculateCommission() {
    BigDecimal rate = new BigDecimal("0.005");
    return calculateGross().multiply(rate);
  }

  @Override
  public BigDecimal calculateTax() {
    BigDecimal tax = new BigDecimal("0");
    return tax;
  }

  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().add(calculateCommission()).add(calculateTax());
  }
}
