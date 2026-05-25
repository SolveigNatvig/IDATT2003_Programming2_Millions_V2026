import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Exchange;
import Model.Player;
import Model.Share;
import Model.Stock;
import Model.Transaction;
import Model.Purchase;
import Model.Sale;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeTest {

    private Exchange exchange;
    private Stock apple;
    private Stock google;
    private Player player;

    @BeforeEach
    void setUp() {
        apple = new Stock("AAPL", "Apple", new BigDecimal("100"));
        google = new Stock("GOOGL", "Google", new BigDecimal("200"));

        List<Stock> stocks = new ArrayList<>();
        stocks.add(apple);
        stocks.add(google);

        exchange = new Exchange("TestExchange", stocks);
        player = new Player("Jane", new BigDecimal("500000"));
    }

    // ---- Positive tests ----

    @Test
    void testGetName() {
        assertEquals("TestExchange", exchange.getName());
    }

    @Test
    void testInitialWeekIsOne() {
        assertEquals(1, exchange.getWeek());
    }

    @Test
    void testAdvanceIncrementsWeek() {
        exchange.advance();
        assertEquals(2, exchange.getWeek());
    }

    @Test
    void testHasStockReturnsTrue() {
        assertTrue(exchange.hasStock("AAPL"));
    }

    @Test
    void testHasStockReturnsFalse() {
        assertFalse(exchange.hasStock("MSFT"));
    }

    @Test
    void testGetStockReturnsCorrectStock() {
        assertEquals(apple, exchange.getStock("AAPL"));
    }

    @Test
    void testGetStockReturnsNullForUnknown() {
        assertNull(exchange.getStock("MSFT"));
    }

    @Test
    void testFindStocksBySymbol() {
        List<Stock> result = exchange.findStocks("AAPL");
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    void testFindStocksByCompanyName() {
        List<Stock> result = exchange.findStocks("e");
        assertEquals(2, result.size());
    }

    @Test
    void testFindStocksNoMatch() {
        List<Stock> result = exchange.findStocks("Samsung");
        assertEquals(0, result.size());
    }

    @Test
    void testFindStocksEmptyTermReturnsAll() {
        List<Stock> result = exchange.findStocks("");
        assertEquals(2, result.size());
    }

    @Test
    void testBuyReturnsCommittedPurchase() {
        Transaction t = exchange.buy("AAPL", new BigDecimal("5"), player);
        assertNotNull(t);
        assertTrue(t.isCommitted());
        assertInstanceOf(Purchase.class, t);
    }

    @Test
    void testBuyDeductsMoneyFromPlayer() {
        BigDecimal before = player.getMoney();
        exchange.buy("AAPL", new BigDecimal("5"), player);
        assertTrue(player.getMoney().compareTo(before) < 0);
    }

    @Test
    void testBuyAddsShareToPortfolio() {
        exchange.buy("AAPL", new BigDecimal("5"), player);
        assertFalse(player.getPortfolio().getShares("AAPL").isEmpty());
    }

    @Test
    void testSellReturnsCommittedSale() {
        exchange.buy("AAPL", new BigDecimal("5"), player);
        Share share = player.getPortfolio().getShares("AAPL").get(0);

        Transaction t = exchange.sell(share, player);
        assertNotNull(t);
        assertTrue(t.isCommitted());
        assertInstanceOf(Sale.class, t);
    }

    @Test
    void testSellAddsMoney() {
        exchange.buy("AAPL", new BigDecimal("5"), player);
        Share share = player.getPortfolio().getShares("AAPL").get(0);
        BigDecimal before = player.getMoney();

        exchange.sell(share, player);
        assertTrue(player.getMoney().compareTo(before) > 0);
    }

    @Test
    void testSellRemovesShareFromPortfolio() {
        exchange.buy("AAPL", new BigDecimal("5"), player);
        Share share = player.getPortfolio().getShares("AAPL").get(0);

        exchange.sell(share, player);
        assertTrue(player.getPortfolio().getShares("AAPL").isEmpty());
    }

    @Test
    void testGetGainersReturnsRequestedLimit() {
        exchange.advance(); // prices must have changed at least once for a meaningful sort
        List<Stock> gainers = exchange.getGainers(1);
        assertEquals(1, gainers.size());
    }

    @Test
    void testGetLosersReturnsRequestedLimit() {
        exchange.advance();
        List<Stock> losers = exchange.getLosers(1);
        assertEquals(1, losers.size());
    }

    // ---- Negative tests ----

    @Test
    void testConstructorNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Exchange(null, List.of(apple))
        );
    }

    @Test
    void testConstructorBlankNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Exchange("  ", List.of(apple))
        );
    }

    @Test
    void testConstructorNullStockListThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Exchange("X", null)
        );
    }

    @Test
    void testConstructorNullStockEntryThrows() {
        List<Stock> withNull = new ArrayList<>();
        withNull.add(apple);
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () ->
            new Exchange("X", withNull)
        );
    }

    @Test
    void testBuyNullSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.buy(null, new BigDecimal("5"), player)
        );
    }

    @Test
    void testBuyUnknownSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.buy("MSFT", new BigDecimal("5"), player)
        );
    }

    @Test
    void testBuyZeroQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.buy("AAPL", BigDecimal.ZERO, player)
        );
    }

    @Test
    void testBuyNegativeQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.buy("AAPL", new BigDecimal("-1"), player)
        );
    }

    @Test
    void testBuyNullPlayerThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.buy("AAPL", new BigDecimal("5"), null)
        );
    }

    @Test
    void testBuyInsufficientFundsThrows() {
        Player poorPlayer = new Player("Broke", new BigDecimal("1"));
        assertThrows(IllegalStateException.class, () ->
            exchange.buy("AAPL", new BigDecimal("5"), poorPlayer)
        );
    }

    @Test
    void testSellNullPlayerThrows() {
        exchange.buy("AAPL", new BigDecimal("5"), player);
        Share share = player.getPortfolio().getShares("AAPL").get(0);
        assertThrows(IllegalArgumentException.class, () ->
            exchange.sell(share, null)
        );
    }

    @Test
    void testSellShareNotInPortfolioThrows() {
        Share unowned = new Share(apple, new BigDecimal("1"), new BigDecimal("100"));
        assertThrows(IllegalStateException.class, () ->
            exchange.sell(unowned, player)
        );
    }

    @Test
    void testAddNullObserverThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            exchange.addObserver(null)
        );
    }
}
