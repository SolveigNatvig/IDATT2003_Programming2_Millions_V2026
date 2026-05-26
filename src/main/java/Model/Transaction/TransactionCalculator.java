package Model.Transaction;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating transaction costs and proceeds.
 * Implementations compute fees and totals for different transaction types
 * (purchases and sales), following the Strategy design pattern.
 */
public interface TransactionCalculator {
  /**
   * Calculates the base cost/revenue without fees.
   *
   * @return the gross amount
   */
  BigDecimal calculateGross();

  /**
   * Calculates the commission fee on the transaction.
   *
   * @return the commission amount
   */
  BigDecimal calculateCommission();

  /**
   * Calculates any applicable taxes on the transaction.
   *
   * @return the tax amount
   */
  BigDecimal calculateTax();

  /**
   * Calculates the final total after all fees and taxes.
   *
   * @return the total transaction amount
   */
  BigDecimal calculateTotal();

}

