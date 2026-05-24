import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Portfolio;
import Model.Share;
import Model.Stock;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioTest {

    private Portfolio portfolio;
    private Stock stock;
    private Share share;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
    }

    // ---- Positive tests ----

    @Test
    void testAddShare() {
        boolean result = portfolio.addShare(share);
        assertTrue(result);
        assertTrue(portfolio.contains(share));
    }

    @Test
    void testAddMultipleShares() {
        Stock stock2 = new Stock("GOOGL", "Google", new BigDecimal("200"));
        Share share2 = new Share(stock2, new BigDecimal("5"), new BigDecimal("190"));

        portfolio.addShare(share);
        portfolio.addShare(share2);

        assertEquals(2, portfolio.getShares().size());
    }

    @Test
    void testRemoveShare() {
        portfolio.addShare(share);
        boolean result = portfolio.removeShare(share);
        assertTrue(result);
        assertFalse(portfolio.contains(share));
    }

    @Test
    void testRemoveShareNotInPortfolioReturnsFalse() {
        boolean result = portfolio.removeShare(share);
        assertFalse(result);
    }

    @Test
    void testGetSharesReturnsAll() {
        portfolio.addShare(share);
        List<Share> shares = portfolio.getShares();
        assertEquals(1, shares.size());
        assertTrue(shares.contains(share));
    }

    @Test
    void testGetSharesIsDefensiveCopy() {
        portfolio.addShare(share);
        List<Share> copy = portfolio.getShares();
        copy.clear();
        assertEquals(1, portfolio.getShares().size());
    }

    @Test
    void testGetSharesBySymbol() {
        Stock stock2 = new Stock("GOOGL", "Google", new BigDecimal("200"));
        Share share2 = new Share(stock2, new BigDecimal("5"), new BigDecimal("190"));

        portfolio.addShare(share);
        portfolio.addShare(share2);

        List<Share> result = portfolio.getShares("AAPL");
        assertEquals(1, result.size());
        assertTrue(result.contains(share));
    }

    @Test
    void testGetSharesBySymbolNoMatch() {
        portfolio.addShare(share);
        List<Share> result = portfolio.getShares("MSFT");
        assertTrue(result.isEmpty());
    }

    @Test
    void testContainsReturnsTrueWhenPresent() {
        portfolio.addShare(share);
        assertTrue(portfolio.contains(share));
    }

    @Test
    void testContainsReturnsFalseWhenAbsent() {
        assertFalse(portfolio.contains(share));
    }

    @Test
    void testGetNetWorthEmptyPortfolio() {
        assertEquals(BigDecimal.ZERO, portfolio.getNetWorth());
    }

    @Test
    void testGetNetWorthWithShares() {
        portfolio.addShare(share);
        // net worth should be positive when portfolio has holdings
        assertTrue(portfolio.getNetWorth().compareTo(BigDecimal.ZERO) > 0);
    }

    // ---- Negative tests ----

    @Test
    void testAddNullShareThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            portfolio.addShare(null)
        );
    }

    @Test
    void testRemoveNullShareThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            portfolio.removeShare(null)
        );
    }

    @Test
    void testContainsNullShareThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            portfolio.contains(null)
        );
    }

    @Test
    void testGetSharesByNullSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            portfolio.getShares((String) null)
        );
    }

    @Test
    void testGetSharesByBlankSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            portfolio.getShares("  ")
        );
    }
}
