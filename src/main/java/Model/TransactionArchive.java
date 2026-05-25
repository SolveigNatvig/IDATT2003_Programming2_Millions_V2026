package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maintains a chronological archive of all transactions executed by a player.
 * The TransactionArchive allows retrieval of transactions by week and type
 * (purchase or sale), and provides statistics about trading activity.
 */
public class TransactionArchive {
  private final List<Transaction> transactions;

  /**
   * Constructs a new empty TransactionArchive.
   */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Adds a transaction to the archive.
   *
   * @param transaction the transaction to add. Cannot be null
   * @return true if the transaction was added successfully
   * @throws IllegalArgumentException if transaction is null
   */
  public boolean add(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Should not be null"); 
    }
        
    return transactions.add(transaction);
  }

  /**
   * Checks if the archive is empty.
   *
   * @return true if no transactions have been recorded
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Returns all transactions that occurred in a specific week.
   *
   * @param week the week number
   * @return a list of transactions in that week
   */
  public List<Transaction> getTransactions(int week) {
    return transactions.stream()
    .filter(transaction -> transaction.getWeek() == week).collect(Collectors.toList());
  }

  /**
   * Returns all purchases that occurred in a specific week.
   *
   * @param week the week number
   * @return a list of purchases in that week
   */
  public List<Purchase> getPurchase(int week) {
    return transactions.stream()
    .filter(transaction -> transaction.getWeek() == week)
    .filter(purchase -> purchase instanceof Purchase)
    .map(transaction -> (Purchase) transaction).collect(Collectors.toList());
  }

  /**
   * Returns all sales that occurred in a specific week.
   *
   * @param week the week number
   * @return a list of sales in that week
   */
  public List<Sale> getSale(int week) {
    return transactions.stream()
    .filter(transaction -> transaction.getWeek() == week)
    .filter(sale -> sale instanceof Sale)
    .map(transaction -> (Sale) transaction).collect(Collectors.toList());
  }

  /**
   * Returns a defensive copy of all transactions in the archive.
   *
   * @return a copy of the transaction list
   */
  public List<Transaction> getAllTransactions() {
    return new ArrayList<>(transactions);
  }

  /**
   * Counts the number of distinct weeks with trading activity.
   *
   * @return the number of unique weeks represented in the archive
   */
  public int countDistinctWeeks() {
    return (int) transactions.stream().map(Transaction::getWeek).distinct().count();
  }
}
