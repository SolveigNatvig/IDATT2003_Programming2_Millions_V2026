import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

public class SaleTest {
    Stock stock;
    Share share;
    Sale sale;

    @BeforeEach
    void setUp(){
        this.stock = new Stock("symbol", "company", new BigDecimal(100));
        this.share = new Share(this.stock, new BigDecimal(20), new BigDecimal(80));
        this.sale = new Sale(this.share, 18);
    } 

    @Test
    void commit_validSale_updatesPlayerState() {
        
        // Arrange
        Player player = new Player("Jane", new BigDecimal(500));
        player.getPortfolio().addShare(this.share);

        // Act
        this.sale.commit(player);

        // Assert
        assertTrue(player.getTransactionArchive().getTransactions(this.sale.getWeek()).contains(this.sale));
        assertFalse(player.getPortfolio().contains(this.share));
        assertTrue(this.sale.isCommitted());
    }

    @Test
    void commit_alreadyCommitted_noAction() {

        // Arrange
        Player player = new Player("Jane", new BigDecimal(500));
        player.getPortfolio().addShare(this.share);
        this.sale.commit(player); 
        BigDecimal moneyAfterFirstCommit = player.getMoney();
        
        // Act
        this.sale.commit(player);

        // Assert
        assertEquals(moneyAfterFirstCommit, player.getMoney());
        assertEquals(1, player.getTransactionArchive().getTransactions(this.sale.getWeek()).size());
        assertTrue(this.sale.isCommitted());
    } 

    
}
