import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PurchaseTest {
    Stock stock;
    Share share;
    Purchase purchase;

    @BeforeEach
    void setUp() {
        this.stock = new Stock("symbol", "company", new BigDecimal(100));
        this.share = new Share(this.stock, new BigDecimal(20), new BigDecimal(50));
        this.purchase = new Purchase(this.share, 18);
    }

    @Test
    void commit_validPurchase_updatesPlayer() {

        // Arrange
        Player player = new Player("Jane", new BigDecimal(500000));
        BigDecimal startingMoney = player.getMoney();

        // Act
        this.purchase.commit(player);

        // Assert
        assertEquals(1, startingMoney.subtract(player.getMoney()).signum());

    }


    
}
