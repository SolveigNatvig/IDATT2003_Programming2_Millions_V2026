package Model;
import java.math.BigDecimal;

public class Purchase extends Transaction {
    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));  
    }

    @Override
    public void commit(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (isCommitted()) {
            throw new IllegalStateException("Purchase has already been committed");
        }

        BigDecimal price = this.getCalculator().calculateTotal();

        if (player.getMoney().compareTo(price) < 0) {
            throw new IllegalStateException(
                "Insufficient funds: required " + price + ", available " + player.getMoney()
            );
        }

        player.withdrawMoney(price);
        player.getPortfolio().addShare(this.getShare());
        player.getTransactionArchive().add(this);
        
        this.committed = true;
    }
}
