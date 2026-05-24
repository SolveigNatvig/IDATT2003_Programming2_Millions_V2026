package Model;

/**
 * Observer interface for the Exchange subject.
 * Implement this interface to receive notifications whenever the exchange
 * state changes (week advances, a trade is committed).
 */
public interface ExchangeObserver {

    /**
     * Called by the Exchange after its state has changed.
     *
     * @param exchange the Exchange that triggered the notification
     */
    void onExchangeUpdated(Exchange exchange);
}
