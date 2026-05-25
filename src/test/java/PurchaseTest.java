import static org.junit.jupiter.api.Assertions.*;

import Model.Player;
import Model.Purchase;
import Model.Share;
import Model.Stock;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Purchase class.
 * Tests purchase transaction handling, commitment, and error handling.
 */
public class PurchaseTest {

  private Stock stock;
  private Share share;
  private Purchase purchase;
  private Player player;

  @BeforeEach
  void setUp() {
    stock = new Stock("AAPL", "Apple", new BigDecimal("100"));
    share = new Share(stock, new BigDecimal("20"), new BigDecimal("50"));
    purchase = new Purchase(share, 1);
    player = new Player("Jane", new BigDecimal("500000"));
  }

  // ---- Positive tests ----

  /**
   * Tests that committing a purchase deducts money from player's balance.
   */
  @Test
  void testCommitDeductsMoneyFromPlayer() {
    BigDecimal before = player.getMoney();
    purchase.commit(player);
    assertTrue(player.getMoney().compareTo(before) < 0);
  }

  /**
   * Tests that committing a purchase adds the share to player's portfolio.
   */
  @Test
  void testCommitAddsShareToPortfolio() {
    purchase.commit(player);
    assertTrue(player.getPortfolio().contains(share));
  }

  /**
   * Tests that committed purchase is recorded in transaction archive.
   */
  @Test
  void testCommitRecordedInArchive() {
    purchase.commit(player);
    assertTrue(player.getTransactionArchive().getAllTransactions().contains(purchase));
  }

  /**
   * Tests that committing a purchase sets the committed flag.
   */
  @Test
  void testCommitSetsCommittedFlag() {
    purchase.commit(player);
    assertTrue(purchase.isCommitted());
  }

  /**
   * Tests that getShare returns the correct share object.
   */
  @Test
  void testGetShareReturnsCorrectShare() {
    assertEquals(share, purchase.getShare());
  }

  /**
   * Tests that getWeek returns the purchase week.
   */
  @Test
  void testGetWeekReturnsCorrectWeek() {
    assertEquals(1, purchase.getWeek());
  }

  // ---- Negative tests ----

  /**
   * Tests that commit throws when given null player.
   */
  @Test
  void testCommitNullPlayerThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        purchase.commit(null)
    );
  }

  /**
   * Tests that commit throws when player has insufficient funds.
   */
  @Test
  void testCommitInsufficientFundsThrows() {
    Player poorPlayer = new Player("Broke", new BigDecimal("1"));
    assertThrows(IllegalStateException.class, () ->
        purchase.commit(poorPlayer)
    );
  }

  /**
   * Tests that commit throws when purchase was already committed.
   */
  @Test
  void testCommitAlreadyCommittedThrows() {
    purchase.commit(player);
    assertThrows(IllegalStateException.class, () ->
        purchase.commit(player)
    );
  }

  /**
   * Tests that Purchase constructor throws when given zero week.
   */
  @Test
  void testConstructorWeekZeroThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Purchase(share, 0)
    );
  }

  /**
   * Tests that Purchase constructor throws when given negative week.
   */
  @Test
  void testConstructorNegativeWeekThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Purchase(share, -5)
    );
  }
}

