package Model;

import java.math.BigDecimal;

/**
 * SaleCalculator class that calculates gross, commission, tax and total.
 */
public class SaleCalculator implements TransactionCalculator{
  private BigDecimal purchasePrice;
  private BigDecimal salesPrice;
  private BigDecimal quantity;

  /**
   * Method with parameters.
   *
   * @param share share from calculator
   */
  public SaleCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.salesPrice = share.getStock().getSalesPrice(); 
    this.quantity = share.getQuantity();
  }

  @Override
  public BigDecimal calculateGross() {
    return this.salesPrice.multiply(this.quantity);
  }

  @Override
  public BigDecimal calculateCommission() {
    BigDecimal rate = new BigDecimal("0.01");
    return calculateGross().multiply(rate);
  }

  /**
   * Method that calculates tax.
   */
  public BigDecimal calculateTax() {
    BigDecimal sellingCost = this.purchasePrice.multiply(this.quantity);
    BigDecimal profit = calculateGross().subtract(calculateCommission()).subtract(sellingCost);
    BigDecimal rate = new BigDecimal("0.3");
    return profit.multiply(rate);
  }

  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
    


}
