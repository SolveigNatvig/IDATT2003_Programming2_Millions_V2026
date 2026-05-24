import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Player;
import Model.Purchase;
import Model.Sale;
import Model.Share;
import Model.Stock;
import Model.TransactionArchive;

import java.math.BigDecimal;
import java.util.List;

public class TransactionArchiveTest {

    private TransactionArchive archive;
    private Stock stock;
    private Share share;
    private Purchase purchase;
    private Sale sale;

    @BeforeEach
    void setUp() {
        archive = new TransactionArchive();
        stock = new Stock("AAPL", "Apple", new BigDecimal("100"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("90"));
        purchase = new Purchase(share, 1);
        sale = new Sale(share, 2);
    }

    // ---- Positive tests ----

    @Test
    void testIsEmptyOnCreation() {
        assertTrue(archive.isEmpty());
    }

    @Test
    void testIsEmptyAfterAdd() {
        archive.add(purchase);
        assertFalse(archive.isEmpty());
    }

    @Test
    void testAddReturnsTrue() {
        assertTrue(archive.add(purchase));
    }

    @Test
    void testGetAllTransactions() {
        archive.add(purchase);
        archive.add(sale);
        assertEquals(2, archive.getAllTransactions().size());
    }

    @Test
    void testGetAllTransactionsIsDefensiveCopy() {
        archive.add(purchase);
        List<?> copy = archive.getAllTransactions();
        copy.clear();
        assertEquals(1, archive.getAllTransactions().size());
    }

    @Test
    void testGetTransactionsByWeek() {
        archive.add(purchase); // week 1
        archive.add(sale);     // week 2

        List<?> week1 = archive.getTransactions(1);
        assertEquals(1, week1.size());
        assertTrue(week1.contains(purchase));
    }

    @Test
    void testGetTransactionsByWeekNoMatch() {
        archive.add(purchase); // week 1
        assertTrue(archive.getTransactions(99).isEmpty());
    }

    @Test
    void testGetPurchaseByWeek() {
        archive.add(purchase); // week 1
        archive.add(sale);     // week 2

        List<Purchase> purchases = archive.getPurchase(1);
        assertEquals(1, purchases.size());
        assertTrue(purchases.contains(purchase));
    }

    @Test
    void testGetPurchaseByWeekExcludesSales() {
        // Add a sale at week 1 — should not appear in getPurchase(1)
        Sale saleWeek1 = new Sale(share, 1);
        archive.add(saleWeek1);

        assertTrue(archive.getPurchase(1).isEmpty());
    }

    @Test
    void testGetSaleByWeek() {
        archive.add(purchase); // week 1
        archive.add(sale);     // week 2

        List<Sale> sales = archive.getSale(2);
        assertEquals(1, sales.size());
        assertTrue(sales.contains(sale));
    }

    @Test
    void testGetSaleByWeekExcludesPurchases() {
        // Add a purchase at week 2 — should not appear in getSale(2)
        Purchase purchaseWeek2 = new Purchase(share, 2);
        archive.add(purchaseWeek2);

        assertTrue(archive.getSale(2).isEmpty());
    }

    @Test
    void testCountDistinctWeeksEmpty() {
        assertEquals(0, archive.countDistinctWeeks());
    }

    @Test
    void testCountDistinctWeeksMultipleTransactionsSameWeek() {
        Purchase purchase2 = new Purchase(share, 1);
        archive.add(purchase);
        archive.add(purchase2);
        assertEquals(1, archive.countDistinctWeeks());
    }

    @Test
    void testCountDistinctWeeksAcrossWeeks() {
        archive.add(purchase); // week 1
        archive.add(sale);     // week 2
        assertEquals(2, archive.countDistinctWeeks());
    }

    // ---- Negative tests ----

    @Test
    void testAddNullThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            archive.add(null)
        );
    }
}
