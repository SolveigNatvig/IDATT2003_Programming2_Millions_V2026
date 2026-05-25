import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Stock;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Unit tests for the Stock class.
 * Tests stock creation, price tracking, and error handling.
 */
public class StockTest {

  private Stock stock;

  @BeforeEach
  void setUp() {
      stock = new Stock("AAPL", "Apple", new BigDecimal("1000"));
  }

  // ---- Positive tests ----

  /**
   * Tests that getSymbol returns the correct stock symbol.
   */
  @Test
  void testGetSymbol() {
      assertEquals("AAPL", stock.getSymbol());
  }

  /**
   * Tests that getCompany returns the correct company name.
   */
  @Test
  void testGetCompany() {
      assertEquals("Apple", stock.getCompany());
  }

  /**
   * Tests that getSalesPrice returns the current stock price.
   */
  @Test
  void testGetSalesPrice() {
      assertEquals(new BigDecimal("1000"), stock.getSalesPrice());
  }

  /**
   * Tests that stock can have zero sales price.
   */
  @Test
  void testGetSalesPriceZero() {
      Stock zeroStock = new Stock("ZERO", "ZeroCorp", new BigDecimal("0"));
      assertEquals(new BigDecimal("0"), zeroStock.getSalesPrice());
  }

  /**
   * Tests that adding a new sales price updates the current price.
   */
  @Test
  void testAddNewSalesPrice() {
      stock.addNewSalesPrice(new BigDecimal("1200"));
      assertEquals(new BigDecimal("1200"), stock.getSalesPrice());
  }

  /**
   * Tests that zero price can be added to price history.
   */
  @Test
  void testAddNewSalesPriceZero() {
      stock.addNewSalesPrice(new BigDecimal("0"));
      assertEquals(new BigDecimal("0"), stock.getSalesPrice());
  }

  /**
   * Tests that historical prices initially contain only the initial price.
   */
  @Test
  void testGetHistoricalPricesInitial() {
      List<BigDecimal> history = stock.getHistoricalPrices();
      assertEquals(1, history.size());
      assertEquals(new BigDecimal("1000"), history.get(0));
  }

  /**
   * Tests that historical prices accumulates all price changes.
   */
  @Test
  void testGetHistoricalPricesAfterUpdates() {
      stock.addNewSalesPrice(new BigDecimal("1100"));
      stock.addNewSalesPrice(new BigDecimal("1200"));
      assertEquals(3, stock.getHistoricalPrices().size());
  }

  /**
   * Tests that getHighestPrice returns the maximum price from history.
   */
  @Test
  void testGetHighestPrice() {
      stock.addNewSalesPrice(new BigDecimal("1500"));
      stock.addNewSalesPrice(new BigDecimal("800"));
      assertEquals(new BigDecimal("1500"), stock.getHighestPrice());
  }

  /**
   * Tests that getLowestPrice returns the minimum price from history.
   */
  @Test
  void testGetLowestPrice() {
      stock.addNewSalesPrice(new BigDecimal("1500"));
      stock.addNewSalesPrice(new BigDecimal("800"));
      assertEquals(new BigDecimal("800"), stock.getLowestPrice());
  }

  /**
   * Tests that getLatestPriceChange returns positive change for price increase.
   */
  @Test
  void testGetLatestPriceChangePositive() {
      stock.addNewSalesPrice(new BigDecimal("1100"));
      assertEquals(new BigDecimal("100"), stock.getLatestPriceChange());
  }

  /**
   * Tests that getLatestPriceChange returns negative change for price decrease.
   */
  @Test
  void testGetLatestPriceChangeNegative() {
      stock.addNewSalesPrice(new BigDecimal("900"));
      assertEquals(new BigDecimal("-100"), stock.getLatestPriceChange());
  }

  /**
   * Tests that price change is zero when only one price exists.
   */
  @Test
  void testGetLatestPriceChangeOnlyOnePrice() {
      assertEquals(BigDecimal.ZERO, stock.getLatestPriceChange());
  }

  /**
   * Tests that getHistoricalPrices returns a defensive copy.
   */
  @Test
  void testHistoricalPricesIsDefensiveCopy() {
      List<BigDecimal> history = stock.getHistoricalPrices();
      history.add(new BigDecimal("9999"));
      assertEquals(1, stock.getHistoricalPrices().size());
  }

  // ---- Negative tests ----

  /**
   * Tests that Stock constructor throws when given null symbol.
   */
  @Test
  void testNullSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock(null, "Apple", new BigDecimal("100"))
      );
  }

  /**
   * Tests that Stock constructor throws when given blank symbol.
   */
  @Test
  void testBlankSymbolThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock("  ", "Apple", new BigDecimal("100"))
      );
  }

  /**
   * Tests that Stock constructor throws when given null company name.
   */
  @Test
  void testNullCompanyThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock("AAPL", null, new BigDecimal("100"))
      );
  }

  /**
   * Tests that Stock constructor throws when given blank company name.
   */
  @Test
  void testBlankCompanyThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock("AAPL", "  ", new BigDecimal("100"))
      );
  }

  /**
   * Tests that Stock constructor throws when given null initial price.
   */
  @Test
  void testNullInitialPriceThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock("AAPL", "Apple", null)
      );
  }

  /**
   * Tests that Stock constructor throws when given negative initial price.
   */
  @Test
  void testNegativeInitialPriceThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          new Stock("AAPL", "Apple", new BigDecimal("-1"))
      );
  }

  /**
   * Tests that addNewSalesPrice throws when given null price.
   */
  @Test
  void testAddNullPriceThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          stock.addNewSalesPrice(null)
      );
  }

  /**
   * Tests that addNewSalesPrice throws when given negative price.
   */
  @Test
  void testAddNegativePriceThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          stock.addNewSalesPrice(new BigDecimal("-10"))
      );
  }
}

