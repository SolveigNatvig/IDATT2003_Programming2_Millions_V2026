import static org.junit.jupiter.api.Assertions.*;

import Model.Exchange;
import Model.Player;
import Model.Purchase;
import Model.Sale;
import Model.Share;
import Model.Stock;
import Model.Transaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Exchange class.
 * Tests core exchange functionality including stock management, buying/selling,
 * market data retrieval, and error handling.
 */
public class ExchangeTest {

  private Exchange exchange;
  private Stock apple;
  private Stock google;
  private Player player;

  @BeforeEach
  void setUp() {
      apple = new Stock("AAPL", "Apple", new BigDecimal("100"));
      google = new Stock("GOOGL", "Google", new BigDecimal("200"));

      List<Stock> stocks = new ArrayList<>();
      stocks.add(apple);
      stocks.add(google);

      exchange = new Exchange("TestExchange", stocks);
      player = new Player("Jane", new BigDecimal("500000"));
  }

  // ---- Positive tests ----

  /**
   * Tests that exchange returns its correct name.
   */
  @Test
  void testGetName() {
      assertEquals("TestExchange", exchange.getName());
  }

  /**
   * Tests that exchange starts at week 1.
   */
  @Test
  void testInitialWeekIsOne() {
      assertEquals(1, exchange.getWeek());
  }

  /**
   * Tests that calling advance increments the week counter.
   */
  @Test
  void testAdvanceIncrementsWeek() {
      exchange.advance();
      assertEquals(2, exchange.getWeek());
  }

  /**
   * Tests that hasStock returns true for a known stock symbol.
   */
  @Test
  void testHasStockReturnsTrue() {
      assertTrue(exchange.hasStock("AAPL"));
  }

  /**
   * Tests that hasStock returns false for an unknown stock symbol.
   */
  @Test
  void testHasStockReturnsFalse() {
      assertFalse(exchange.hasStock("MSFT"));
  }

  /**
   * Tests that getStock returns the correct Stock object for a given symbol.
   */
  @Test
  void testGetStockReturnsCorrectStock() {
      assertEquals(apple, exchange.getStock("AAPL"));
  }

  /**
   * Tests that getStock returns null for an unknown symbol.
   */
  @Test
  void testGetStockReturnsNullForUnknown() {
      assertNull(exchange.getStock("MSFT"));
  }

  /**
   * Tests that findStocks correctly locates stocks by symbol.
   */
  @Test
  void testFindStocksBySymbol() {
      List<Stock> result = exchange.findStocks("AAPL");
      assertEquals(1, result.size());
      assertEquals("AAPL", result.get(0).getSymbol());
  }

  /**
   * Tests that findStocks correctly locates stocks by partial company name match.
   */
  @Test
  void testFindStocksByCompanyName() {
      List<Stock> result = exchange.findStocks("e");
      assertEquals(2, result.size());
  }

  /**
   * Tests that findStocks returns empty list when no stocks match search term.
   */
  @Test
  void testFindStocksNoMatch() {
      List<Stock> result = exchange.findStocks("Samsung");
      assertEquals(0, result.size());
  }

  /**
   * Tests that findStocks with empty search term returns all stocks.
   */
  @Test
  void testFindStocksEmptyTermReturnsAll() {
      List<Stock> result = exchange.findStocks("");
      assertEquals(2, result.size());
  }

  /**
   * Tests that buying a stock returns a committed Purchase transaction.
   */
  @Test
  void testBuyReturnsCommittedPurchase() {
      Transaction t = exchange.buy("AAPL", new BigDecimal("5"), player);
      assertNotNull(t);
      assertTrue(t.isCommitted());
      assertInstanceOf(Purchase.class, t);
  }

  /**
   * Tests that buying stock deducts the correct amount from player's balance.
   */
  @Test
  void testBuyDeductsMoneyFromPlayer() {
      BigDecimal before = player.getMoney();
      exchange.buy("AAPL", new BigDecimal("5"), player);
      assertTrue(player.getMoney().compareTo(before) < 0);
  }

  /**
   * Tests that buying stock adds the share to player's portfolio.
   */
  @Test
  void testBuyAddsShareToPortfolio() {
      exchange.buy("AAPL", new BigDecimal("5"), player);
      assertFalse(player.getPortfolio().getShares("AAPL").isEmpty());
  }

  /**
   * Tests that selling a share returns a committed Sale transaction.
   */
  @Test
  void testSellReturnsCommittedSale() {
      exchange.buy("AAPL", new BigDecimal("5"), player);
      Share share = player.getPortfolio().getShares("AAPL").get(0);

      Transaction t = exchange.sell(share, player);
      assertNotNull(t);
      assertTrue(t.isCommitted());
      assertInstanceOf(Sale.class, t);
  }

  /**
   * Tests that selling stock adds the correct amount to player's balance.
   */
  @Test
  void testSellAddsMoney() {
      exchange.buy("AAPL", new BigDecimal("5"), player);
      Share share = player.getPortfolio().getShares("AAPL").get(0);
      BigDecimal before = player.getMoney();

      exchange.sell(share, player);
      assertTrue(player.getMoney().compareTo(before) > 0);
  }

  /**
   * Tests that selling stock removes the share from player's portfolio.
   */
  @Test
  void testSellRemovesShareFromPortfolio() {
      exchange.buy("AAPL", new BigDecimal("5"), player);
      Share share = player.getPortfolio().getShares("AAPL").get(0);

      exchange.sell(share, player);
      assertTrue(player.getPortfolio().getShares("AAPL").isEmpty());
  }

  /**
   * Tests that getGainers returns the requested number of gaining stocks.
   */
  @Test
  void testGetGainersReturnsRequestedLimit() {
      exchange.advance(); // prices must have changed at least once for a meaningful sort
      List<Stock> gainers = exchange.getGainers(1);
      assertEquals(1, gainers.size());
  }

  /**
   * Tests that getLosers returns the requested number of losing stocks.
   */
  @Test
  void testGetLosersReturnsRequestedLimit() {
      exchange.advance();
      List<Stock> losers = exchange.getLosers(1);
      assertEquals(1, losers.size());
  }

  // ---- Negative tests ----

  /**
   * Tests that Exchange constructor throws when given a null name.
   */
  @Test
  void testConstructorNullNameThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Exchange(null, List.of(apple))
      );
  }

  /**
   * Tests that Exchange constructor throws when given a blank name.
   */
  @Test
  void testConstructorBlankNameThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Exchange("  ", List.of(apple))
      );
  }

  /**
   * Tests that Exchange constructor throws when given a null stock list.
   */
  @Test
  void testConstructorNullStockListThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Exchange("X", null)
      );
  }

  /**
   * Tests that Exchange constructor throws when stock list contains null entry.
   */
  @Test
  void testConstructorNullStockEntryThrows() {
      List<Stock> withNull = new ArrayList<>();
      withNull.add(apple);
      withNull.add(null);
      assertThrows(IllegalArgumentException.class, () ->
          new Exchange("X", withNull)
      );
  }

  /**
   * Tests that buy throws when given a null symbol.
   */
  @Test
  void testBuyNullSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.buy(null, new BigDecimal("5"), player)
      );
  }

  /**
   * Tests that buy throws when given an unknown stock symbol.
   */
  @Test
  void testBuyUnknownSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.buy("MSFT", new BigDecimal("5"), player)
      );
  }

  /**
   * Tests that buy throws when given zero quantity.
   */
  @Test
  void testBuyZeroQuantityThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.buy("AAPL", BigDecimal.ZERO, player)
      );
  }

  /**
   * Tests that buy throws when given negative quantity.
   */
  @Test
  void testBuyNegativeQuantityThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.buy("AAPL", new BigDecimal("-1"), player)
      );
  }

  /**
   * Tests that buy throws when given a null player.
   */
  @Test
  void testBuyNullPlayerThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.buy("AAPL", new BigDecimal("5"), null)
      );
  }

  /**
   * Tests that buy throws when player has insufficient funds.
   */
  @Test
  void testBuyInsufficientFundsThrows() {
      Player poorPlayer = new Player("Broke", new BigDecimal("1"));
      assertThrows(IllegalStateException.class, () ->
          exchange.buy("AAPL", new BigDecimal("5"), poorPlayer)
      );
  }

  /**
   * Tests that sell throws when given a null player.
   */
  @Test
  void testSellNullPlayerThrows() {
      exchange.buy("AAPL", new BigDecimal("5"), player);
      Share share = player.getPortfolio().getShares("AAPL").get(0);
      assertThrows(IllegalArgumentException.class, () ->
          exchange.sell(share, null)
      );
  }

  /**
   * Tests that sell throws when share is not in player's portfolio.
   */
  @Test
  void testSellShareNotInPortfolioThrows() {
      Share unowned = new Share(apple, new BigDecimal("1"), new BigDecimal("100"));
      assertThrows(IllegalStateException.class, () ->
          exchange.sell(unowned, player)
      );
  }

  /**
   * Tests that addObserver throws when given a null observer.
   */
  @Test
  void testAddNullObserverThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          exchange.addObserver(null)
      );
  }
}

