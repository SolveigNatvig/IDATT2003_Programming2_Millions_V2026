import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Player;
import Model.Purchase;
import Model.Share;
import Model.Stock;

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

    @Test
    void testCommitDeductsMoneyFromPlayer() {
        BigDecimal before = player.getMoney();
        purchase.commit(player);
        assertTrue(player.getMoney().compareTo(before) < 0);
    }

    @Test
    void testCommitAddsShareToPortfolio() {
        purchase.commit(player);
        assertTrue(player.getPortfolio().contains(share));
    }

    @Test
    void testCommitRecordedInArchive() {
        purchase.commit(player);
        assertTrue(player.getTransactionArchive().getAllTransactions().contains(purchase));
    }

    @Test
    void testCommitSetsCommittedFlag() {
        purchase.commit(player);
        assertTrue(purchase.isCommitted());
    }

    @Test
    void testGetShareReturnsCorrectShare() {
        assertEquals(share, purchase.getShare());
    }

    @Test
    void testGetWeekReturnsCorrectWeek() {
        assertEquals(1, purchase.getWeek());
    }

    // ---- Negative tests ----

    @Test
    void testCommitNullPlayerThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            purchase.commit(null)
        );
    }

    @Test
    void testCommitInsufficientFundsThrows() {
        Player poorPlayer = new Player("Broke", new BigDecimal("1"));
        assertThrows(IllegalStateException.class, () ->
            purchase.commit(poorPlayer)
        );
    }

    @Test
    void testCommitAlreadyCommittedThrows() {
        purchase.commit(player);
        assertThrows(IllegalStateException.class, () ->
            purchase.commit(player)
        );
    }

    @Test
    void testConstructorWeekZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Purchase(share, 0)
        );
    }

    @Test
    void testConstructorNegativeWeekThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Purchase(share, -5)
        );
    }
}
