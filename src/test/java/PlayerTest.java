import static org.junit.jupiter.api.Assertions.*;

import Model.Player;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the Player class.
 * Tests player creation, money management, portfolio, and error handling.
 */
public class PlayerTest {

  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player("Jane", new BigDecimal("1000"));
  }

  // ---- Positive tests ----

  /**
   * Tests that player name is stored and retrieved correctly.
   */
  @Test
  void testGetName() {
    assertEquals("Jane", player.getName());
  }

  /**
   * Tests that player's initial money equals starting capital.
   */
  @Test
  void testGetMoneyMatchesStartingCapital() {
    assertEquals(new BigDecimal("1000"), player.getMoney());
  }

  /**
   * Tests that adding money increases player's balance.
   */
  @Test
  void testAddMoney() {
    player.addMoney(new BigDecimal("500"));
    assertEquals(new BigDecimal("1500"), player.getMoney());
  }

  /**
   * Tests that adding zero money does not change balance.
   */
  @Test
  void testAddMoneyZero() {
    player.addMoney(BigDecimal.ZERO);
    assertEquals(new BigDecimal("1000"), player.getMoney());
  }

  /**
   * Tests that withdrawing money decreases player's balance.
   */
  @Test
  void testWithdrawMoney() {
    player.withdrawMoney(new BigDecimal("400"));
    assertEquals(new BigDecimal("600"), player.getMoney());
  }

  /**
   * Tests that withdrawing zero money does not change balance.
   */
  @Test
  void testWithdrawMoneyZero() {
    player.withdrawMoney(BigDecimal.ZERO);
    assertEquals(new BigDecimal("1000"), player.getMoney());
  }

  /**
   * Tests that player's portfolio is initially empty.
   */
  @Test
  void testPortfolioInitiallyEmpty() {
    assertTrue(player.getPortfolio().getShares().isEmpty());
  }

  /**
   * Tests that player's transaction archive is initially empty.
   */
  @Test
  void testTransactionArchiveInitiallyEmpty() {
    assertTrue(player.getTransactionArchive().isEmpty());
  }

  /**
   * Tests that player can be created with zero starting money.
   */
  @Test
  void testStartingMoneyZeroAllowed() {
    Player broke = new Player("Broke", BigDecimal.ZERO);
    assertEquals(BigDecimal.ZERO, broke.getMoney());
  }

  // ---- Negative tests ----

  /**
   * Tests that Player constructor throws when given a null name.
   */
  @Test
  void testNullNameThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player(null, new BigDecimal("1000"))
    );
  }

  /**
   * Tests that Player constructor throws when given a blank name.
   */
  @Test
  void testBlankNameThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("  ", new BigDecimal("1000"))
    );
  }

  /**
   * Tests that Player constructor throws when given null starting money.
   */
  @Test
  void testNullStartingMoneyThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Jane", null)
    );
  }

  /**
   * Tests that Player constructor throws when given negative starting money.
   */
  @Test
  void testNegativeStartingMoneyThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Jane", new BigDecimal("-1"))
    );
  }

  /**
   * Tests that addMoney throws when given null amount.
   */
  @Test
  void testAddNullAmountThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(null)
    );
  }

  /**
   * Tests that addMoney throws when given negative amount.
   */
  @Test
  void testAddNegativeAmountThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(new BigDecimal("-100"))
    );
  }

  /**
   * Tests that withdrawMoney throws when given null amount.
   */
  @Test
  void testWithdrawNullAmountThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(null)
    );
  }

  /**
   * Tests that withdrawMoney throws when given negative amount.
   */
  @Test
  void testWithdrawNegativeAmountThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(new BigDecimal("-100"))
    );
  }
}

