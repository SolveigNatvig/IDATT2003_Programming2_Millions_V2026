package Model.Player;

import Model.Portfolio;
import Model.Transaction.TransactionArchive;
import java.math.BigDecimal;


/**
 * Represents a player in the stock trading game.
 * A Player manages their personal portfolio of shares, cash balance, and transaction
 * history. Players can buy and sell stocks, and their performance is tracked through
 * status levels based on trading activity and net worth growth.
 */
public class Player {
  private String name;
  private BigDecimal startingMoney;
  private BigDecimal money;
  private Portfolio portfolio;
  private TransactionArchive transactionArchive;

  /**
   * Constructs a new Player with the given name and starting capital.
   *
   * @param name the player's name. Cannot be null or blank
   * @param startingMoney the initial cash balance. Cannot be null or negative
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public Player(String name, BigDecimal startingMoney) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank");
    }
    if (startingMoney == null) {
      throw new IllegalArgumentException("Starting money cannot be null");
    }
    if (startingMoney.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Starting money cannot be negative");
    }

    this.name = name;        
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
  }

  /**
   * Returns the player's name.
   *
   * @return the player's name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the player's current cash balance.
   *
   * @return the current amount of money available
   */
  public BigDecimal getMoney() {
    return this.money;
  }

  /**
   * Returns the player's initial cash balance.
   * 
   * @return the players money at the start
   */
  public BigDecimal getStartingMoney() {
    return this.startingMoney;
  }

  /**
   * Adds the specified amount to the player's cash balance.
   *
   * @param amount the amount to add. Cannot be null or negative
   * @throws IllegalArgumentException if amount is invalid
   */
  public void addMoney(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount to add cannot be negative");
    }
    this.money = this.money.add(amount);
  }

  /**
   * Removes the specified amount from the player's cash balance.
   *
   * @param amount the amount to withdraw. Cannot be null or negative
   * @throws IllegalArgumentException if amount is invalid
   */
  public void withdrawMoney(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount to withdraw cannot be negative");
    }
    this.money = this.money.subtract(amount);
  }

  /**
   * Returns the player's portfolio.
   *
   * @return the portfolio containing held shares
   */
  public Portfolio getPortfolio() {
    return this.portfolio;
  }

  /**
   * Returns the player's transaction archive.
   *
   * @return the archive of all transactions
   */
  public TransactionArchive getTransactionArchive() {
    return this.transactionArchive;
  }


  /**
  * Returns the player's current status based on trading activity and net worth performance.   
  * Status levels:
  * - NOVICE: Starting level, no requirements
  * - INVESTOR: At least 10 weeks of trading AND net worth increased by at least 20%
  * - SPECULATOR: At least 20 weeks of trading AND net worth at least doubled
  *
  * @return the player's current PlayerStatus
  */
  public PlayerStatus getStatus() {
    // Calculate total net worth: current cash + portfolio value
    BigDecimal totalNetWorth = this.money.add(this.portfolio.getNetWorth());
        
    // Get number of weeks with trading activity
    int weeksActive = this.transactionArchive.countDistinctWeeks();
        
    // Check for SPECULATOR status: 20+ weeks AND net worth doubled
    if (weeksActive >= 20 && totalNetWorth.compareTo(this
        .startingMoney.multiply(new BigDecimal("2"))) >= 0) {
      return PlayerStatus.SPECULATOR;
    }
        
    // Check for INVESTOR status: 10+ weeks AND net worth increased by 20%
    if (weeksActive >= 10 && totalNetWorth.compareTo(this
        .startingMoney.multiply(new BigDecimal("1.2"))) >= 0) {
      return PlayerStatus.INVESTOR;
    }
        
    // Default: NOVICE
    return PlayerStatus.NOVICE;
  }

}
