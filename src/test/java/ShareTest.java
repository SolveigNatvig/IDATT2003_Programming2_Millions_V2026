import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Share;
import Model.Stock;

import java.math.BigDecimal;

public class ShareTest {

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple", new BigDecimal("150"));
    }

    // ---- Positive tests ----

    @Test
    void testShareConstructorNotNull() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
        assertNotNull(share);
    }

    @Test
    void testGetStock() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
        assertEquals(stock, share.getStock());
    }

    @Test
    void testGetQuantity() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
        assertEquals(new BigDecimal("10"), share.getQuantity());
    }

    @Test
    void testGetPurchasePrice() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("140"));
        assertEquals(new BigDecimal("140"), share.getPurchasePrice());
    }

    @Test
    void testPurchasePriceZeroAllowed() {
        // buying at price 0 is an edge case but not invalid
        Share share = new Share(stock, new BigDecimal("5"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, share.getPurchasePrice());
    }

    // ---- Negative tests ----

    @Test
    void testNullStockThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(null, new BigDecimal("10"), new BigDecimal("100"))
        );
    }

    @Test
    void testNullQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(stock, null, new BigDecimal("100"))
        );
    }

    @Test
    void testZeroQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(stock, BigDecimal.ZERO, new BigDecimal("100"))
        );
    }

    @Test
    void testNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(stock, new BigDecimal("-5"), new BigDecimal("100"))
        );
    }

    @Test
    void testNullPurchasePriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(stock, new BigDecimal("10"), null)
        );
    }

    @Test
    void testNegativePurchasePriceThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Share(stock, new BigDecimal("10"), new BigDecimal("-50"))
        );
    }
}
