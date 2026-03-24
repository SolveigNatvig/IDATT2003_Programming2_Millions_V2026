import java.math.BigDecimal;

public interface TransactionCalculator {
    // Methods in interface is automatically public abstract.
    BigDecimal calculateGross();
    BigDecimal calculateCommission();
    BigDecimal calculateTax();
    BigDecimal calculateTotal();
}

