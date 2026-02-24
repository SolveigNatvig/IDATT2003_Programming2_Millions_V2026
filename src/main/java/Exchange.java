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

        for (Stock stock : stockMap.values()) {
            if (stock.getSymbol().contains(searchTerm)
                    || stock.getCompany().contains(searchTerm)) {
                result.add(stock);
            }
        }

        return result;
        }

    // public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    //     return;
    // }

    // public Transaction sell(Share share, Player player) {
    //     return;
    // }

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
