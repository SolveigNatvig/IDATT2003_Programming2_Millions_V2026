import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Player;
import Model.Sale;
import Model.Share;
import Model.Stock;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

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

    @Test
    void testCommitAddsMoney() {
        BigDecimal before = player.getMoney();
        sale.commit(player);
        assertTrue(player.getMoney().compareTo(before) > 0);
    }

    @Test
    void testCommitRemovesShareFromPortfolio() {
        sale.commit(player);
        assertFalse(player.getPortfolio().contains(share));
    }

    @Test
    void testCommitRecordedInArchive() {
        sale.commit(player);
        assertTrue(player.getTransactionArchive().getAllTransactions().contains(sale));
    }

    @Test
    void testCommitSetsCommittedFlag() {
        sale.commit(player);
        assertTrue(sale.isCommitted());
    }

    @Test
    void testGetShareReturnsCorrectShare() {
        assertEquals(share, sale.getShare());
    }

    @Test
    void testGetWeekReturnsCorrectWeek() {
        assertEquals(1, sale.getWeek());
    }

    // ---- Negative tests ----

    @Test
    void testCommitNullPlayerThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            sale.commit(null)
        );
    }

    @Test
    void testCommitAlreadyCommittedThrows() {
        sale.commit(player);
        assertThrows(IllegalStateException.class, () ->
            sale.commit(player)
        );
    }

    @Test
    void testCommitShareNotInPortfolioThrows() {
        Player otherPlayer = new Player("Bob", new BigDecimal("500"));
        assertThrows(IllegalStateException.class, () ->
            sale.commit(otherPlayer)
        );
    }

    @Test
    void testConstructorWeekZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Sale(share, 0)
        );
    }

    @Test
    void testConstructorNegativeWeekThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Sale(share, -3)
        );
    }
}
