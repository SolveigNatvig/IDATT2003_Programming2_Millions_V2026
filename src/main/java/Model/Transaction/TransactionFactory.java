package Model.Transaction;

import Model.Purchase.Purchase;
import Model.Sale.Sale;
import Model.Share;


/**
 * Factory for creating transaction objects.
 * Uses the Factory pattern to encapsulate transaction creation logic,
 * allowing clients to create Purchase and Sale transactions without
 * knowing implementation details.
 */
public class TransactionFactory {

  /**
   * Creates a new Purchase transaction.
   *
   * @param share the share to purchase. Cannot be null
   * @param week the week when the purchase occurs. Must be at least 1
   * @return a new Purchase transaction
   */
  public static Transaction createPurchase(Share share, int week) {
    return new Purchase(share, week);
  }

  /**
   * Creates a new Sale transaction.
   *
   * @param share the share to sell. Cannot be null
   * @param week the week when the sale occurs. Must be at least 1
   * @return a new Sale transaction
   */
  public static Transaction createSale(Share share, int week) {
    return new Sale(share, week);
  }
    
}
