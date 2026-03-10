import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ExchangeTest {

    private Exchange exchange;
    private Stock apple;
    private Stock google;

    @Before
    public void setUp() {
        apple = new Stock("AAPL", "Apple", new BigDecimal("100"));
        google = new Stock("GOOGL", "Google", new BigDecimal("200"));

        List<Stock> stocks = new ArrayList<>();
        stocks.add(apple);
        stocks.add(google);

        exchange = new Exchange("ABC", stocks);
    }

    @Test
    public void testFindStocksBySymbol() {
        List<Stock> result = exchange.findStocks("AAPL");

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    public void testFindStocksByCompanyNames() {
        List<Stock> result = exchange.findStocks("e");

        assertEquals(2, result.size());
    }

    @Test
    public void testFindStocksNotInList() {
        List<Stock> result = exchange.findStocks("Samsung");

        assertEquals(0, result.size());
    }
    
}
