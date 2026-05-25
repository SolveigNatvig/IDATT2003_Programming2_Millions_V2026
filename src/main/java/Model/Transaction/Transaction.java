package Model.Transaction;

import Model.Player.Player;
import Model.Share;


/**
 * Abstract base class for all transactions (purchases and sales).
 * A Transaction represents a trading action taken by a player on a specific week.
 * Transactions use the Strategy pattern via TransactionCalculator to compute costs
 * and can be committed to apply their effects to the player's portfolio.
 */
public abstract class Transaction {
  private Share share;
  private int week;
  private TransactionCalculator calculator;
  protected boolean committed;

  /**
   * Constructs a Transaction for the specified share in the given week.
   *
   * @param share the share being traded. Cannot be null
   * @param week the week number when the transaction occurs. Must be at least 1
   * @param calculator the calculator for transaction costs. Cannot be null
   * @throws IllegalArgumentException if any parameter is invalid
   */
  protected Transaction(Share share, int week, TransactionCalculator calculator) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (week < 1) {
      throw new IllegalArgumentException("Week must be at least 1");
    }
    if (calculator == null) {
      throw new IllegalArgumentException("Calculator cannot be null");
    }

    this.share = share;
    this.week = week;
    this.calculator = calculator;
  }

  /**
   * Returns the share being traded in this transaction.
   *
   * @return the share
   */
  public Share getShare() {
    return this.share;
  }

  /**
   * Returns the week number when this transaction occurs.
   *
   * @return the week number
   */
  public int getWeek() {
    return this.week;
  }

  /**
   * Returns the calculator used to compute transaction costs.
   *
   * @return the transaction calculator
   */
  public TransactionCalculator getCalculator() {
    return this.calculator;
  }

  /**
   * Returns whether this transaction has been committed.
   *
   * @return true if committed; false otherwise
   */
  public boolean isCommitted() {
    return this.committed;
  }

  /**
   * Commits this transaction, applying its effects to the player's portfolio.
   *
   * @param player the player executing the transaction. Cannot be null
   * @throws IllegalArgumentException if player is null
   * @throws IllegalStateException if the transaction has already been committed
   */
  public abstract void commit(Player player);
    
}
