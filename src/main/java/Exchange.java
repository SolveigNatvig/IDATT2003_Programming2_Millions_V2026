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

    public Exchange(String name, List<Stock> stocks) {
        this.name = name;
        this.week = 1;
        this.stockMap = new HashMap<>();
        this.random = new Random();

        for (Stock stock : stocks) {
            stockMap.put(stock.getSymbol(), stock);
        }

    }
    
    public String getName() {
        return name;
    }

    public int getWeek() {
        return week;
    }

    public boolean hasStock(String symbol) {
        return stockMap.containsKey(symbol);
    }

    public Stock getStock(String symbol) {
        return stockMap.get(symbol);
    }

    public List<Stock> findStocks(String searchTerm) {
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
        Stock stock = getStock(symbol);
        
         // unngå nullpointerexception
        if (stock == null) {
            return null; 
        }

        // lager en ny "andel" basert på nåværende salgspris
        Share shareToBuy = new Share(stock, quantity, stock.getSalesPrice());
        
        // oppretter kjøpstransaksjonen for den uka
        Purchase purchase = new Purchase(shareToBuy, this.week);
        
        // committer til player
        purchase.commit(player);
        
        return purchase;
    }

    public Transaction sell(Share share, Player player) {
        // unngå nullpointerexception
        if (share == null) {
            return null;
        }

        // oppretter salgstransaksjonen for den uka
        Sale sale = new Sale(share, this.week);
        
        // commiter til player
        sale.commit(player);
        
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
    }



}
