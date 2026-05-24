import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Stock;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class StockTest {

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple", new BigDecimal("1000"));
    }

    // ---- Positive tests ----

    @Test
    void testGetSymbol() {
        assertEquals("AAPL", stock.getSymbol());
    }

    @Test
    void testGetCompany() {
        assertEquals("Apple", stock.getCompany());
    }

    @Test
    void testGetSalesPrice() {
        assertEquals(new BigDecimal("1000"), stock.getSalesPrice());
    }

    @Test
    void testGetSalesPriceZero() {
        Stock zeroStock = new Stock("ZERO", "ZeroCorp", new BigDecimal("0"));
        assertEquals(new BigDecimal("0"), zeroStock.getSalesPrice());
    }

    @Test
    void testAddNewSalesPrice() {
        stock.addNewSalesPrice(new BigDecimal("1200"));
        assertEquals(new BigDecimal("1200"), stock.getSalesPrice());
    }

    @Test
    void testAddNewSalesPriceZero() {
        stock.addNewSalesPrice(new BigDecimal("0"));
        assertEquals(new BigDecimal("0"), stock.getSalesPrice());
    }

    @Test
    void testGetHistoricalPricesInitial() {
        List<BigDecimal> history = stock.getHistoricalPrices();
        assertEquals(1, history.size());
        assertEquals(new BigDecimal("1000"), history.get(0));
    }

    @Test
    void testGetHistoricalPricesAfterUpdates() {
        stock.addNewSalesPrice(new BigDecimal("1100"));
        stock.addNewSalesPrice(new BigDecimal("1200"));
        assertEquals(3, stock.getHistoricalPrices().size());
    }

    @Test
    void testGetHighestPrice() {
        stock.addNewSalesPrice(new BigDecimal("1500"));
        stock.addNewSalesPrice(new BigDecimal("800"));
        assertEquals(new BigDecimal("1500"), stock.getHighestPrice());
    }

    @Test
    void testGetLowestPrice() {
        stock.addNewSalesPrice(new BigDecimal("1500"));
        stock.addNewSalesPrice(new BigDecimal("800"));
        assertEquals(new BigDecimal("800"), stock.getLowestPrice());
    }

    @Test
    void testGetLatestPriceChangePositive() {
        stock.addNewSalesPrice(new BigDecimal("1100"));
        assertEquals(new BigDecimal("100"), stock.getLatestPriceChange());
    }

    @Test
    void testGetLatestPriceChangeNegative() {
        stock.addNewSalesPrice(new BigDecimal("900"));
        assertEquals(new BigDecimal("-100"), stock.getLatestPriceChange());
    }

    @Test
    void testGetLatestPriceChangeOnlyOnePrice() {
        assertEquals(BigDecimal.ZERO, stock.getLatestPriceChange());
    }

    @Test
    void testHistoricalPricesIsDefensiveCopy() {
        List<BigDecimal> history = stock.getHistoricalPrices();
        history.add(new BigDecimal("9999"));
        assertEquals(1, stock.getHistoricalPrices().size());
    }

    // ---- Negative tests ----

    @Test
    void testNullSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock(null, "Apple", new BigDecimal("100"))
        );
    }

    @Test
    void testBlankSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock("  ", "Apple", new BigDecimal("100"))
        );
    }

    @Test
    void testNullCompanyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock("AAPL", null, new BigDecimal("100"))
        );
    }

    @Test
    void testBlankCompanyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock("AAPL", "  ", new BigDecimal("100"))
        );
    }

    @Test
    void testNullInitialPriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock("AAPL", "Apple", null)
        );
    }

    @Test
    void testNegativeInitialPriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Stock("AAPL", "Apple", new BigDecimal("-1"))
        );
    }

    @Test
    void testAddNullPriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            stock.addNewSalesPrice(null)
        );
    }

    @Test
    void testAddNegativePriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            stock.addNewSalesPrice(new BigDecimal("-10"))
        );
    }
}
