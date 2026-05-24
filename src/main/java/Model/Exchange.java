package Model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Exchange {

    private final String name;
    private int week;
    private final Map<String, Stock> stockMap;
    private final Random random;

    // Registered observers
    private final List<ExchangeObserver> observers = new ArrayList<>();

    public Exchange(String name, List<Stock> stocks) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Exchange name cannot be null or blank");
        }
        if (stocks == null) {
            throw new IllegalArgumentException("Stock list cannot be null");
        }

        this.name = name;
        this.week = 1;
        this.stockMap = new HashMap<>();
        this.random = new Random();

        for (Stock stock : stocks) {
            if (stock == null) {
                throw new IllegalArgumentException("Stock list must not contain null entries");
            }
            stockMap.put(stock.getSymbol(), stock);
        }
    }

    // ---- Observer ----

    public void addObserver(ExchangeObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ExchangeObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ExchangeObserver observer : observers) {
            observer.onExchangeUpdated(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getWeek() {
        return week;
    }

    public boolean hasStock(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or blank");
        }
        return stockMap.containsKey(symbol);
    }

    public Stock getStock(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or blank");
        }
        return stockMap.get(symbol);
    }

    public List<Stock> findStocks(String searchTerm) {
        if (searchTerm == null) {
            throw new IllegalArgumentException("Search term cannot be null");
        }
        List<Stock> result = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();

        for (Stock stock : stockMap.values()) {
            if (stock.getSymbol().toLowerCase().contains(lowerSearch)
                    || stock.getCompany().toLowerCase().contains(lowerSearch)) {
                result.add(stock);
            }
        }

        return result;
    }

    public Transaction buy(String symbol, BigDecimal quantity, Player player) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or blank");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
 
        Stock stock = getStock(symbol);
        if (stock == null) {
            throw new IllegalArgumentException("No stock found with symbol: " + symbol);
        }

        // lager en ny "andel" basert på nåværende salgspris
        Share shareToBuy = new Share(stock, quantity, stock.getSalesPrice());
        Purchase purchase = new Purchase(shareToBuy, this.week);
        purchase.commit(player);

        notifyObservers();

        return purchase;
    }

    public Transaction sell(Share share, Player player) {

        return sell(share, share.getQuantity(), player);
    }

    public Transaction sell(Share originalShare, BigDecimal sellQuantity, Player player) {
        if (originalShare == null || sellQuantity == null) {
            throw new IllegalArgumentException("Share and quantity cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        // Kan ikke selge mer enn man eier
        if (sellQuantity.compareTo(originalShare.getQuantity()) > 0) {
            throw new IllegalArgumentException("Cannot sell more shares than owned");
        }


        Share shareToSell;

        if (sellQuantity.compareTo(originalShare.getQuantity()) < 0) {

            /**
             * Delsalg:
             * Original mengde - mengde som selges = gjenværende mengde
             */

            player.getPortfolio().removeShare(originalShare);

            BigDecimal remainderShare = originalShare.getQuantity().subtract(sellQuantity);

            player.getPortfolio().addShare(new Share(originalShare.getStock(), remainderShare, originalShare.getPurchasePrice()));

            /**
             * Legger delmengden midlertidig til i portfolio slik at Sale.commit() finner den
             */

            shareToSell = new Share(originalShare.getStock(), sellQuantity, originalShare.getPurchasePrice());

            player.getPortfolio().addShare(shareToSell);

        }

        else {
            // Fullstendig salg: hele andelen selges som normalt
            shareToSell = originalShare;
        }

        // Salgstransaksjon
        Transaction sale = TransactionFactory.createSale(shareToSell, this.week);

        sale.commit(player);
        notifyObservers();
        return sale;
    }

    public void advance() {
        
        week++;

        for (Stock stock : stockMap.values()) { // henter stock-objektene

            BigDecimal currentPrice = stock.getSalesPrice(); // henter siste pris fra Stock

            double changePercent = (random.nextDouble() - 0.5) * 0.1; 

            BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent)); 

            BigDecimal newPrice = currentPrice.add(change); 

            if (newPrice.compareTo(BigDecimal.ZERO) > 0) { // unngå negativ pris
                stock.addNewSalesPrice(newPrice);
            }
        }

        notifyObservers();
    }

    public List<Stock> getGainers(int limit) { // viser "vinnerne"
        return stockMap.values().stream()
            .sorted((s1, s2) -> s2.getLatestPriceChange().compareTo(s1.getLatestPriceChange()))
            .limit(limit)
            .toList();
    }

    public List<Stock> getLosers(int limit) { // viser "taperne"
        return stockMap.values().stream()
            .sorted((s1, s2) -> s1.getLatestPriceChange().compareTo(s2.getLatestPriceChange()))
            .limit(limit)
            .toList();
    }
}
