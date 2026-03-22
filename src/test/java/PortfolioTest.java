import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

public class PortfolioTest {

    @Test
    void testAddSharePortfolio() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        boolean result = portfolio.addShare(share);

        assertTrue(result);
        assertTrue(portfolio.contains(share));
    }

    @Test
    void testRemoveSharePortfolio() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        portfolio.addShare(share);

        boolean result = portfolio.removeShare(share);

        assertTrue(result);
        assertFalse(portfolio.contains(share));
    }

    @Test
    void testGetSharesPortfolio() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        portfolio.addShare(share);

        var shares = portfolio.getShares();

        assertEquals(1, shares.size());
        assertTrue(shares.contains(share));
    }

    @Test
    void testGetSharesBySymbolPortfolio() {
        Portfolio portfolio = new Portfolio();

        Stock stock1 = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Stock stock2 = new Stock("GOOGL", "Google", new BigDecimal("200"));

        Share share1 = new Share(stock1, new BigDecimal("10"), new BigDecimal("140"));
        Share share2 = new Share(stock2, new BigDecimal("10"), new BigDecimal("190"));

        portfolio.addShare(share1);
        portfolio.addShare(share2);

        var result = portfolio.getShares("AAPL");

        assertEquals(1, result.size());
        assertTrue(result.contains(share1));
    }

    @Test
    void testContainsPortfolio() {
        Portfolio portfolio = new Portfolio();

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        portfolio.addShare(share);

        assertTrue(portfolio.contains(share));
    }

    
}
