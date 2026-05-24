package Model;
                
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Portfolio class of the player.
 */
public class Portfolio {

  private final List<Share> shares;

  public Portfolio() {
    this.shares = new ArrayList<>();
  }

  /**
   * Method to add share.
   *
   * @param share the share being added
   * @return returns the added share 
   */
  public boolean addShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.add(share);
  }

  /**
   * Method to remove share.
   *
   * @param share the share being removed
   * @return returns the removed share
   */
  public boolean removeShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.remove(share);
  }

  public List<Share> getShares() {
    return new ArrayList<>(shares);
  }

  /**
   * Method to get all shares.
   *
   * @param symbol the symbol for each stock
   * @return the shares of stocks
   */
  public List<Share> getShares(String symbol) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be null or blank");
    }
    return shares.stream()
        .filter(share -> share.getStock().getSymbol().equals(symbol))
        .toList();
  }

  /**
   * Method to check if contains share.
   *
   * @param share the share 
   * @return the share if not null
   */
  public boolean contains(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.contains(share);
  }

  /**
   * Method to get the new worth.
   *
   * @return returns the net worth
   */
  public BigDecimal getNetWorth() {
    return shares.stream()
      .map(share -> new SaleCalculator(share).calculateTotal())
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
    
}
