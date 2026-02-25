import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;


public class StockTest {

    @Test 
    public void testGetSalesPrice() {
        Stock stock = new Stock("AAPL", "APPLE", new BigDecimal("1000"));
        assertEquals(new BigDecimal("1000"), stock.getSalesPrice());
    }
    
    @Test 
    public void testAddNewSalesPrice() {
        Stock stock = new Stock("AAPL", "APPLE", new BigDecimal("1000"));
        stock.addNewSalesPrice(new BigDecimal("1200"));
        assertEquals(new BigDecimal("1200"), stock.getSalesPrice());
    }

    @Test 
    public void testGetSalesPriceNone() {
        Stock stock = new Stock("AAPL", "APPLE", new BigDecimal("0"));
        assertEquals(new BigDecimal("0"), stock.getSalesPrice());
    }

    @Test 
    public void testAddNewSalesPriceNone() {
        Stock stock = new Stock("AAPL", "APPLE", new BigDecimal("1000"));
        stock.addNewSalesPrice(new BigDecimal("0"));
        assertEquals(new BigDecimal("0"), stock.getSalesPrice());
    }

    
}
