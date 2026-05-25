import static org.junit.jupiter.api.Assertions.*;
import Model.Portfolio;
import Model.Share;
import Model.Stock;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Portfolio class.
 * Tests share management, portfolio queries, net worth calculation, and error
 * handling.
 */
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

  /**
   * Tests that adding a share to portfolio returns true and succeeds.
   */
  @Test
  void testAddShare() {
      boolean result = portfolio.addShare(share);
      assertTrue(result);
      assertTrue(portfolio.contains(share));
  }

  /**
   * Tests that portfolio can store multiple shares from different stocks.
   */
  @Test
  void testAddMultipleShares() {
      Stock stock2 = new Stock("GOOGL", "Google", new BigDecimal("200"));
      Share share2 = new Share(stock2, new BigDecimal("5"), new BigDecimal("190"));

      portfolio.addShare(share);
      portfolio.addShare(share2);

      assertEquals(2, portfolio.getShares().size());
  }

  /**
   * Tests that removing a share from portfolio returns true and succeeds.
   */
  @Test
  void testRemoveShare() {
      portfolio.addShare(share);
      boolean result = portfolio.removeShare(share);
      assertTrue(result);
      assertFalse(portfolio.contains(share));
  }

  /**
   * Tests that removing a share not in portfolio returns false.
   */
  @Test
  void testRemoveShareNotInPortfolioReturnsFalse() {
      boolean result = portfolio.removeShare(share);
      assertFalse(result);
  }

  /**
   * Tests that getShares returns all shares in portfolio.
   */
  @Test
  void testGetSharesReturnsAll() {
      portfolio.addShare(share);
      List<Share> shares = portfolio.getShares();
      assertEquals(1, shares.size());
      assertTrue(shares.contains(share));
  }

  /**
   * Tests that getShares returns a defensive copy that cannot modify internal
   * state.
   */
  @Test
  void testGetSharesIsDefensiveCopy() {
      portfolio.addShare(share);
      List<Share> copy = portfolio.getShares();
      copy.clear();
      assertEquals(1, portfolio.getShares().size());
  }

  /**
   * Tests that getShares by symbol returns only shares of that stock.
   */
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

  /**
   * Tests that getShares by symbol returns empty list when no matches.
   */
  @Test
  void testGetSharesBySymbolNoMatch() {
      portfolio.addShare(share);
      List<Share> result = portfolio.getShares("MSFT");
      assertTrue(result.isEmpty());
  }

  /**
   * Tests that contains returns true for shares in portfolio.
   */
  @Test
  void testContainsReturnsTrueWhenPresent() {
      portfolio.addShare(share);
      assertTrue(portfolio.contains(share));
  }

  /**
   * Tests that contains returns false for shares not in portfolio.
   */
  @Test
  void testContainsReturnsFalseWhenAbsent() {
      assertFalse(portfolio.contains(share));
  }

  /**
   * Tests that empty portfolio has zero net worth.
   */
  @Test
  void testGetNetWorthEmptyPortfolio() {
      assertEquals(BigDecimal.ZERO, portfolio.getNetWorth());
  }

  /**
   * Tests that portfolio with shares has positive net worth.
   */
  @Test
  void testGetNetWorthWithShares() {
      portfolio.addShare(share);
      // net worth should be positive when portfolio has holdings
      assertTrue(portfolio.getNetWorth().compareTo(BigDecimal.ZERO) > 0);
  }

  // ---- Negative tests ----

  /**
   * Tests that addShare throws when given null share.
   */
  @Test
  void testAddNullShareThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          portfolio.addShare(null)
      );
  }

  /**
   * Tests that removeShare throws when given null share.
   */
  @Test
  void testRemoveNullShareThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          portfolio.removeShare(null)
      );
  }

  /**
   * Tests that contains throws when given null share.
   */
  @Test
  void testContainsNullShareThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          portfolio.contains(null)
      );
  }

  /**
   * Tests that getShares throws when given null symbol.
   */
  @Test
  void testGetSharesByNullSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          portfolio.getShares((String) null)
      );
  }

  /**
   * Tests that getShares throws when given blank symbol.
   */
  @Test
  void testGetSharesByBlankSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          portfolio.getShares("  ")
      );
  }
}
