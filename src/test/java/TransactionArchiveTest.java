import static org.junit.jupiter.api.Assertions.*;

import Model.Player;
import Model.Purchase;
import Model.Sale;
import Model.Share;
import Model.Stock;
import Model.TransactionArchive;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the TransactionArchive class.
 * Tests transaction recording, retrieval, filtering, and error handling.
 */
public class TransactionArchiveTest {

  private TransactionArchive archive;
  private Stock stock;
  private Share share;
  private Purchase purchase;
  private Sale sale;

  @BeforeEach
  void setUp() {
  archive = new TransactionArchive();
  stock = new Stock("AAPL", "Apple", new BigDecimal("100"));
  share = new Share(stock, new BigDecimal("10"), new BigDecimal("90"));
  purchase = new Purchase(share, 1);
  sale = new Sale(share, 2);
  }

  // ---- Positive tests ----

  /**
   * Tests that archive is empty immediately after creation.
   */
  @Test
  void testIsEmptyOnCreation() {
  assertTrue(archive.isEmpty());
  }

  /**
   * Tests that isEmpty returns false after adding a transaction.
   */
  @Test
  void testIsEmptyAfterAdd() {
  archive.add(purchase);
  assertFalse(archive.isEmpty());
  }

  /**
   * Tests that add method returns true for successful addition.
   */
  @Test
  void testAddReturnsTrue() {
  assertTrue(archive.add(purchase));
  }

  /**
   * Tests that getAllTransactions returns all stored transactions.
   */
  @Test
  void testGetAllTransactions() {
      archive.add(purchase);
      archive.add(sale);
      assertEquals(2, archive.getAllTransactions().size());
  }

  /**
   * Tests that getAllTransactions returns a defensive copy.
   */
  @Test
  void testGetAllTransactionsIsDefensiveCopy() {
      archive.add(purchase);
      List<?> copy = archive.getAllTransactions();
      copy.clear();
      assertEquals(1, archive.getAllTransactions().size());
  }

  /**
   * Tests that getTransactions filters transactions by week number.
   */
  @Test
  void testGetTransactionsByWeek() {
      archive.add(purchase); // week 1
      archive.add(sale);     // week 2

      List<?> week1 = archive.getTransactions(1);
      assertEquals(1, week1.size());
      assertTrue(week1.contains(purchase));
  }

  /**
   * Tests that getTransactions returns empty list for non-existent week.
   */
  @Test
  void testGetTransactionsByWeekNoMatch() {
      archive.add(purchase); // week 1
      assertTrue(archive.getTransactions(99).isEmpty());
  }

  /**
   * Tests that getPurchase returns only purchase transactions for week.
   */
  @Test
  void testGetPurchaseByWeek() {
      archive.add(purchase); // week 1
      archive.add(sale);     // week 2

      List<Purchase> purchases = archive.getPurchase(1);
      assertEquals(1, purchases.size());
      assertTrue(purchases.contains(purchase));
  }

  /**
   * Tests that getPurchase does not return sale transactions.
   */
  @Test
  void testGetPurchaseByWeekExcludesSales() {
      // Add a sale at week 1 — should not appear in getPurchase(1)
      Sale saleWeek1 = new Sale(share, 1);
      archive.add(saleWeek1);

      assertTrue(archive.getPurchase(1).isEmpty());
  }

  /**
   * Tests that getSale returns only sale transactions for week.
   */
  @Test
  void testGetSaleByWeek() {
      archive.add(purchase); // week 1
      archive.add(sale);     // week 2

      List<Sale> sales = archive.getSale(2);
      assertEquals(1, sales.size());
      assertTrue(sales.contains(sale));
  }

  /**
   * Tests that getSale does not return purchase transactions.
   */
  @Test
  void testGetSaleByWeekExcludesPurchases() {
      // Add a purchase at week 2 — should not appear in getSale(2)
      Purchase purchaseWeek2 = new Purchase(share, 2);
      archive.add(purchaseWeek2);

      assertTrue(archive.getSale(2).isEmpty());
  }

  /**
   * Tests that countDistinctWeeks returns zero for empty archive.
   */
  @Test
  void testCountDistinctWeeksEmpty() {
      assertEquals(0, archive.countDistinctWeeks());
  }

  /**
   * Tests that countDistinctWeeks counts only distinct weeks.
   */
  @Test
  void testCountDistinctWeeksMultipleTransactionsSameWeek() {
      Purchase purchase2 = new Purchase(share, 1);
      archive.add(purchase);
      archive.add(purchase2);
      assertEquals(1, archive.countDistinctWeeks());
  }

  /**
   * Tests that countDistinctWeeks correctly counts across multiple weeks.
   */
  @Test
  void testCountDistinctWeeksAcrossWeeks() {
      archive.add(purchase); // week 1
      archive.add(sale);     // week 2
      assertEquals(2, archive.countDistinctWeeks());
  }

  // ---- Negative tests ----

  /**
   * Tests that add throws when given null transaction.
   */
  @Test
  void testAddNullThrows() {
      assertThrows(IllegalArgumentException.class, () ->
          archive.add(null)
      );
  }
}

