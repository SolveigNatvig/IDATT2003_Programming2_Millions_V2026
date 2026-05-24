package Model;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Stock {

    private final String symbol;
    private final String company;
    private final List<BigDecimal> prices;

    public Stock(String symbol, String company, BigDecimal salesPrice) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or blank");
        }
        if (company == null || company.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null or blank");
        }
        if (salesPrice == null) {
            throw new IllegalArgumentException("Initial sales price cannot be null");
        }
        if (salesPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial sales price cannot be negative");
        }

        this.symbol = symbol;
        this.company = company;
        this.prices = new ArrayList<>();
        this.prices.add(salesPrice);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompany() {
        return company;
    }

    public BigDecimal getSalesPrice() {
        return prices.get(prices.size() - 1);
    }
    
    public void addNewSalesPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        prices.add(price);
    }

    public List<BigDecimal> getHistoricalPrices() {
        return new ArrayList<>(prices); // returnerer en kopi for å beskytte selve listen
    }

    public BigDecimal getHighestPrice() {
        return prices.stream()
            .reduce(prices.get(0), BigDecimal::max);
    }

    public BigDecimal getLowestPrice() {
        return prices.stream()
            .reduce(prices.get(0), BigDecimal::min);
    }

    public BigDecimal getLatestPriceChange() {
        if (prices.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal latest = prices.get(prices.size() - 1);
        BigDecimal previous = prices.get(prices.size() - 2);
        return latest.subtract(previous);
    }
}
