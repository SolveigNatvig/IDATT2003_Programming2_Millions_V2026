import static org.junit.jupiter.api.Assertions.*;

import Model.Share;
import Model.Stock;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



/**
 * Unit tests for the Share class.
 * Tests share creation, property access, and error handling.
 */
public class ShareTest {

  private Stock stock;

  @BeforeEach
  void setUp() {
    stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
  }

  // ---- Positive tests ----

  /**
   * Tests that Share constructor creates a non-null object.
   */
  @Test
  void testShareConstructorNotNull() {
    Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
    assertNotNull(share);
  }

  /**
   * Tests that getStock returns the correct stock object.
   */
  @Test
  void testGetStock() {
    Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
    assertEquals(stock, share.getStock());
  }

  /**
   * Tests that getQuantity returns the correct quantity.
   */
  @Test
  void testGetQuantity() {
    Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
    assertEquals(new BigDecimal("10"), share.getQuantity());
  }

  /**
   * Tests that getPurchasePrice returns the correct purchase price.
   */
  @Test
  void testGetPurchasePrice() {
    Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
    assertEquals(new BigDecimal("140"), share.getPurchasePrice());
  }

  /**
   * Tests that share can be created with zero purchase price.
   */
  @Test
  void testPurchasePriceZeroAllowed() {
    // buying at price 0 is an edge case but not invalid
    Share share = new Share(stock, new BigDecimal("5"), BigDecimal.ZERO);
    assertEquals(BigDecimal.ZERO, share.getPurchasePrice());
  }

  // ---- Negative tests ----

  /**
   * Tests that Share constructor throws when given null stock.
   */
  @Test
  void testNullStockThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(null, new BigDecimal("10"), new BigDecimal("100"))
    );
  }

  /**
   * Tests that Share constructor throws when given null quantity.
   */
  @Test
  void testNullQuantityThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(stock, null, new BigDecimal("100"))
    );
  }

  /**
   * Tests that Share constructor throws when given zero quantity.
   */
  @Test
  void testZeroQuantityThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(stock, BigDecimal.ZERO, new BigDecimal("100"))
    );
  }

  /**
   * Tests that Share constructor throws when given negative quantity.
   */
  @Test
  void testNegativeQuantityThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(stock, new BigDecimal("-5"), new BigDecimal("100"))
    );
  }

  /**
   * Tests that Share constructor throws when given null purchase price.
   */
  @Test
  void testNullPurchasePriceThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(stock, new BigDecimal("10"), null)
    );
  }

  /**
   * Tests that Share constructor throws when given negative purchase price.
   */
  @Test
  void testNegativePurchasePriceThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(stock, new BigDecimal("10"), new BigDecimal("-50"))
    );
  }
}

