package Model.Sale;

import Model.Player.Player;
import Model.Share;
import Model.Transaction.Transaction;
import java.math.BigDecimal;

/**
 * Represents a stock sale transaction.
 * A Sale transaction represents a player selling shares from their portfolio.
 * When committed, it adds money to the player's account, removes the shares from
 * their portfolio, and records the transaction in the archive.
 */
public class Sale extends Transaction {
  /**
   * Constructs a new Sale transaction.
   *
   * @param share the share to sell. Cannot be null
   * @param week the week number when the sale occurs. Must be at least 1
   */
  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  /**
   * Commits this sale transaction, crediting funds and removing shares from the portfolio.
   *
   * @param player the player executing the sale. Cannot be null
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException if sale has already been committed or if the
   *         player does not own the share being sold
   */
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
