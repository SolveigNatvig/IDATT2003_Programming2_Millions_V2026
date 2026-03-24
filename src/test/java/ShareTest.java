import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;


public class ShareTest {

    @Test
    void testShareConstructor() {

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        BigDecimal quantity = new BigDecimal("10");
        BigDecimal purchasePrice = new BigDecimal("140");

        Share share = new Share(stock, quantity, purchasePrice);
        assertNotNull(share);
    }

    @Test
    void testGetStock() {

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        Stock result = share.getStock();

        assertEquals(stock, result);
    }

    @Test
    void testGetQuantity() {

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        BigDecimal result = share.getQuantity();

        assertEquals(new BigDecimal("10"), result);
    }

    @Test
    void testGetPurchasePrice() {

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));

        BigDecimal result = share.getPurchasePrice();

        assertEquals(new BigDecimal("140"), result);
    }

    @Test
    void testNullStock() {

        Share share = new Share(null, new BigDecimal("10"), new BigDecimal("100"));

        assertNull(share.getStock());
    }

    @Test
    void testNegativePrice() {

        Stock stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("-50"));

        assertEquals(new BigDecimal("-50"), share.getPurchasePrice());
    }

    
}
