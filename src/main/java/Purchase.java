import java.math.BigDecimal;

public class Purchase extends Transaction {
    public Purchase(Share share, int week) {
        super(share, week, new PurchaseCalculator(share));  
    }

    @Override
    public void commit(Player player) {
        BigDecimal price = this.getShare().getPurchasePrice();

        if (isCommitted() || (player.getMoney().compareTo(price) < 0)) {
            return;
        }

        player.withdrawMoney(price);
        player.getPortfolio().addShare(getShare());
        player.getTransactionArchive().add(this);
        
        this.committed = true;
    }
}
