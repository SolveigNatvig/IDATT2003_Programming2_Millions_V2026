package Model;

/**
 * Observer interface for receiving notifications from the Exchange.
 * Implementations are notified whenever the Exchange state changes,
 * allowing the View and other components to stay synchronized.
 */
public interface ExchangeObserver {

  /**
   * Called by the Exchange when its state changes.
   *
   * @param exchange the Exchange that triggered the notification
   */
  void onExchangeUpdated(Exchange exchange);
}
