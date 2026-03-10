import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

public class PortfolioTest {

    @Test
    void testAddShare() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        boolean result = portfolio.addShare(share);

        assertTrue(result);
        assertTrue(portfolio.contains(share));
    }

    @Test
    void testRemoveShare() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        portfolio.addShare(share);

        boolean result = portfolio.removeShare(share);

        assertTrue(result);
        assertFalse(portfolio.contains(share));
    }

    
}
