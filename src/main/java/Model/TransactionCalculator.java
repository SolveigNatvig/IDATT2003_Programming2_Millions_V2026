package Model;

import java.math.BigDecimal;

/**
 * TransactionCalculator class.
 */
public interface TransactionCalculator {
  // Methods in interface is automatically public abstract.
  BigDecimal calculateGross();

  BigDecimal calculateCommission();

  BigDecimal calculateTax();

  BigDecimal calculateTotal();

}

