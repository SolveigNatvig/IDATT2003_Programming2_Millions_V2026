import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PurchaseCalculatorTest {
    Share share;
    Stock stock;

    @BeforeEach
    void SetUp() {
        stock = new Stock("symbol", "company", new BigDecimal(12));
        share = new Share(stock, new BigDecimal(10), new BigDecimal(7));
    }

    @Test
    void calculateGross_givenShare_shouldReturnExpectedGross() {
        
        // Arrange
        BigDecimal expectedGross = new BigDecimal("70");

        // Act
        PurchaseCalculator calculator = new PurchaseCalculator(share);
        BigDecimal gross = calculator.calculateGross();

        // Assert
        assertTrue(gross.compareTo(expectedGross) == 0);
    }

    @Test
    void calculateCommission_shouldReturnExpectedCommission() {

        // Arrange
        PurchaseCalculator calculator = new PurchaseCalculator(share);
        BigDecimal expectedCommission = new BigDecimal("0.35");

        // Act
        BigDecimal commission = calculator.calculateCommission();

        // Assert
        assertTrue(commission.compareTo(expectedCommission) == 0);
    }

    @Test
    void calculateTax_shouldReturnExpectedTax() {

        // Arrange
        PurchaseCalculator calculator = new PurchaseCalculator(share);
        BigDecimal expectedTax = new BigDecimal("0"); 

        // Act
        BigDecimal tax = calculator.calculateTax();

        // Assert 
        assertTrue(tax.compareTo(expectedTax) == 0);
    }

    @Test
    void calculateTotal_shouldReturnTotalCosts() {
        
        // Arrange
        PurchaseCalculator calculator = new PurchaseCalculator(share);
        BigDecimal expectedTotal = new BigDecimal("70.35");

        // Act
        BigDecimal total = calculator.calculateTotal();

        // Assert
        assertTrue(total.compareTo(expectedTotal) == 0);
    }
    
}
