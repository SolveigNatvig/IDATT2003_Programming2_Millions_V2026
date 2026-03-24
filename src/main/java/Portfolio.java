import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private final List<Share> shares;

    public Portfolio() {
        this.shares = new ArrayList<>();
    }

    public boolean addShare(Share share) {
        return shares.add(share);
    }

    public boolean removeShare(Share share) {
        return shares.remove(share);
    }

    public List<Share> getShares() {
        return new ArrayList<>(shares);
    }

    public List<Share> getShares(String symbol) {
        List<Share> result = new ArrayList<>();
        for (Share share : shares) {
            if (share.getStock().getSymbol().equals(symbol)) {
                result.add(share);
            }
        }
        return result;
    }

    public boolean contains(Share share) {
        return shares.contains(share);
    }

    public BigDecimal getNetWorth() {
        BigDecimal total = BigDecimal.ZERO;
        for (Share share : shares) {
            SaleCalculator calc = new SaleCalculator(share);
            total = total.add(calc.calculateTotal());
        }
        return total;
    }
    
}
