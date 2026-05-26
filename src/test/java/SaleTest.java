import static org.junit.jupiter.api.Assertions.*;

import Model.Player.Player;
import Model.Sale.Sale;
import Model.Share;
import Model.Stock;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the Sale class.
 * Tests sale transaction handling, commitment, and error handling.
 */
public class SaleTest {

  private Stock stock;
  private Share share;
  private Sale sale;
  private Player player;

  @BeforeEach
  void setUp() {
    stock = new Stock("AAPL", "Apple", new BigDecimal("100"));
    share = new Share(stock, new BigDecimal("20"), new BigDecimal("80"));
    sale = new Sale(share, 1);
    player = new Player("Jane", new BigDecimal("500"));
    player.getPortfolio().addShare(share);
  }

  // ---- Positive tests ----

  /**
   * Tests that committing a sale adds money to player's balance.
   */
  @Test
  void testCommitAddsMoney() {
    BigDecimal before = player.getMoney();
    sale.commit(player);
    assertTrue(player.getMoney().compareTo(before) > 0);
  }

  /**
   * Tests that committing a sale removes the share from player's portfolio.
   */
  @Test
  void testCommitRemovesShareFromPortfolio() {
    sale.commit(player);
    assertFalse(player.getPortfolio().contains(share));
  }

  /**
   * Tests that committed sale is recorded in transaction archive.
   */
  @Test
  void testCommitRecordedInArchive() {
    sale.commit(player);
    assertTrue(player.getTransactionArchive().getAllTransactions().contains(sale));
  }

  /**
   * Tests that committing a sale sets the committed flag.
   */
  @Test
  void testCommitSetsCommittedFlag() {
    sale.commit(player);
    assertTrue(sale.isCommitted());
  }

  /**
   * Tests that getShare returns the correct share object.
   */
  @Test
  void testGetShareReturnsCorrectShare() {
    assertEquals(share, sale.getShare());
  }

  /**
   * Tests that getWeek returns the sale week.
   */
  @Test
  void testGetWeekReturnsCorrectWeek() {
    assertEquals(1, sale.getWeek());
  }

  // ---- Negative tests ----

  /**
   * Tests that commit throws when given null player.
   */
  @Test
  void testCommitNullPlayerThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        sale.commit(null)
    );
  }

  /**
   * Tests that commit throws when sale was already committed.
   */
  @Test
  void testCommitAlreadyCommittedThrows() {
    sale.commit(player);
    assertThrows(IllegalStateException.class, () ->
        sale.commit(player)
    );
  }

  /**
   * Tests that commit throws when share is not in player's portfolio.
   */
  @Test
  void testCommitShareNotInPortfolioThrows() {
    Player otherPlayer = new Player("Bob", new BigDecimal("500"));
    assertThrows(IllegalStateException.class, () ->
        sale.commit(otherPlayer)
    );
  }

  /**
   * Tests that Sale constructor throws when given zero week.
   */
  @Test
  void testConstructorWeekZeroThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Sale(share, 0)
    );
  }

  /**
   * Tests that Sale constructor throws when given negative week.
   */
  @Test
  void testConstructorNegativeWeekThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Sale(share, -3)
    );
  }
}

