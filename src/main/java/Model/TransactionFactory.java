package Model;

/**
 * Factory for creating transaction objects
 */
public class TransactionFactory {

    /**
     * Create purchase transaction
     */
    public static Transaction createPurchase(Share share, int week) {
        return new Purchase(share, week);
    }

    /**
     * Create sale transaction
     */
    public static Transaction createSale(Share share, int week) {
        return new Sale(share, week);
    }
    
}
