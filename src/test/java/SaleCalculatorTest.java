import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SaleCalculatorTest {
    Share share;
    Stock stock;

    @BeforeEach
    void SetUp() {
        stock = new Stock("symbol", "company", new BigDecimal(12));
        share = new Share(stock, new BigDecimal(10), new BigDecimal(7));
    }

    // calculateGross()
    @Test
    void calculateGross_givenShare_shouldReturnExpectedGross() {
        
        // Arrange
        BigDecimal expectedGross = new BigDecimal("120");

        // Act
        SaleCalculator calculator = new SaleCalculator(share);
        BigDecimal gross = calculator.calculateGross();

        // Assert
        assertEquals(expectedGross, gross);
    }

    @Test
    void calculateGross_givenQuantityZero_shouldReturnZero() {

        // Arrange
        Share share1 = new Share(stock, new BigDecimal("0"), new BigDecimal("7"));
        SaleCalculator calculator = new SaleCalculator(share1);
        BigDecimal expectedGross = new BigDecimal("0");

        // Act
        BigDecimal gross = calculator.calculateGross();

        // Assert
        assertEquals(expectedGross, gross);
    }

    @Test
    void calculateGross_givenSalesPriceZero_shouldReturnZero() {

        // Arrange
        Stock stock1 = new Stock("symbol", "company", new BigDecimal("0"));
        Share share1 = new Share(stock1, new BigDecimal("10"), new BigDecimal("7"));
        SaleCalculator calculator = new SaleCalculator(share1);
        BigDecimal expectedGross = new BigDecimal("0");

        // Act
        BigDecimal gross = calculator.calculateGross();

        // Assert
        assertEquals(expectedGross, gross);
    }

    // Teste for null?


    // calculateCommission
    @Test
    void calculateCommission_shouldReturnExpectedCommision() {

        // Arrange
        SaleCalculator calculator = new SaleCalculator(share);
        BigDecimal expectedCommission = new BigDecimal("1.2");

        // Act
        BigDecimal commission = calculator.calculateCommission();

        // Assert
        assertTrue(commission.compareTo(expectedCommission) == 0);
    }

    // Negative test....

    // calculateTax()
    @Test
    void calculateTax_shouldReturnExpectedTax() {

        // Arrange
        SaleCalculator calculator = new SaleCalculator(share);
        BigDecimal expectedTax = new BigDecimal("14.64");

        // Act
        BigDecimal tax = calculator.calculateTax();   

        // Assert
        assertTrue(tax.compareTo(expectedTax) == 0);
    }

    // Negative test....

    // calculateTotal()
    @Test 
    void calculateTotal_shouldReturnTotalCosts() {

        // Arrange
        SaleCalculator calculator = new SaleCalculator(share);
        BigDecimal expectedTotal = new BigDecimal("104.16");

        // Act
        BigDecimal total = calculator.calculateTotal();

        // Assert
        assertTrue(total.compareTo(expectedTotal) == 0);
    }

    // Negative tests....




}
