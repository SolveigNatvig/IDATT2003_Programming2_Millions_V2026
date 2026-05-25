import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Player;

import java.math.BigDecimal;

public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Jane", new BigDecimal("1000"));
    }

    // ---- Positive tests ----

    @Test
    void testGetName() {
        assertEquals("Jane", player.getName());
    }

    @Test
    void testGetMoneyMatchesStartingCapital() {
        assertEquals(new BigDecimal("1000"), player.getMoney());
    }

    @Test
    void testAddMoney() {
        player.addMoney(new BigDecimal("500"));
        assertEquals(new BigDecimal("1500"), player.getMoney());
    }

    @Test
    void testAddMoneyZero() {
        player.addMoney(BigDecimal.ZERO);
        assertEquals(new BigDecimal("1000"), player.getMoney());
    }

    @Test
    void testWithdrawMoney() {
        player.withdrawMoney(new BigDecimal("400"));
        assertEquals(new BigDecimal("600"), player.getMoney());
    }

    @Test
    void testWithdrawMoneyZero() {
        player.withdrawMoney(BigDecimal.ZERO);
        assertEquals(new BigDecimal("1000"), player.getMoney());
    }

    @Test
    void testPortfolioInitiallyEmpty() {
        assertTrue(player.getPortfolio().getShares().isEmpty());
    }

    @Test
    void testTransactionArchiveInitiallyEmpty() {
        assertTrue(player.getTransactionArchive().isEmpty());
    }

    @Test
    void testStartingMoneyZeroAllowed() {
        Player broke = new Player("Broke", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, broke.getMoney());
    }

    // ---- Negative tests ----

    @Test
    void testNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Player(null, new BigDecimal("1000"))
        );
    }

    @Test
    void testBlankNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Player("  ", new BigDecimal("1000"))
        );
    }

    @Test
    void testNullStartingMoneyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Player("Jane", null)
        );
    }

    @Test
    void testNegativeStartingMoneyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Player("Jane", new BigDecimal("-1"))
        );
    }

    @Test
    void testAddNullAmountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            player.addMoney(null)
        );
    }

    @Test
    void testAddNegativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            player.addMoney(new BigDecimal("-100"))
        );
    }

    @Test
    void testWithdrawNullAmountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            player.withdrawMoney(null)
        );
    }

    @Test
    void testWithdrawNegativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            player.withdrawMoney(new BigDecimal("-100"))
        );
    }
}
