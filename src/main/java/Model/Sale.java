package Model;

import java.math.BigDecimal;

/**
 * Sale class for whenever the player makes a sale.
 */
public class Sale extends Transaction {
  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  public void commit(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (isCommitted()) {
      throw new IllegalStateException("Sale has already been committed");
    }
    if (!player.getPortfolio().contains(this.getShare())) {
      throw new IllegalStateException(
      "Share not found in player's portfolio: " + this.getShare().getStock().getSymbol()
      );
    }

    BigDecimal price = getCalculator().calculateTotal();

    player.addMoney(price);
    player.getPortfolio().removeShare(this.getShare());
    player.getTransactionArchive().add(this);

    this.committed = true;
  }
}
