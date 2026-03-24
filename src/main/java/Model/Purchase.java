package Model;
import java.math.BigDecimal;

public class Purchase extends Transaction {
    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));  
    }

    @Override
    public void commit(Player player) {
        BigDecimal price = this.getCalculator().calculateTotal();

        if (isCommitted() || (player.getMoney().compareTo(price) < 0)) {
            return;
        }

        player.withdrawMoney(price);
        player.getPortfolio().addShare(this.getShare());
        player.getTransactionArchive().add(this);
        
        this.committed = true;
    }
}
