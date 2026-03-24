import java.math.BigDecimal;

public class Sale extends Transaction {
    public Sale(Share share, int week) {
        super(share, week, new SaleCalculator(share));
    }

    public void commit(Player player) {
        BigDecimal price = getCalculator().calculateTotal();

        if (isCommitted() || !player.getPortfolio().contains(this.getShare())) {
            return;
        }

        player.addMoney(price);
        player.getPortfolio().removeShare(this.getShare());
        player.getTransactionArchive().add(this);

        this.committed = true;
    }
}
