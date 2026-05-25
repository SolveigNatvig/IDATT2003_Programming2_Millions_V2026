package Model;

import java.math.BigDecimal;

/**
 * Represents a stock purchase transaction.
 * A Purchase transaction represents a player buying shares. When committed,
 * it deducts money from the player's account, adds the shares to their portfolio,
 * and records the transaction in the archive.
 */
public class Purchase extends Transaction {
  /**
   * Constructs a new Purchase transaction.
   *
   * @param share the share to purchase. Cannot be null
   * @param week the week number when the purchase occurs. Must be at least 1
   */
  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));  
  }

  /**
   * Commits this purchase transaction, deducting funds and adding shares to the portfolio.
   *
   * @param player the player executing the purchase. Cannot be null
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException if purchase has already been committed or if
   *         the player has insufficient funds
   */
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
