package Model;
import java.math.BigDecimal;

public class Player {
    private String name;
    private BigDecimal startingMoney;
    private BigDecimal money;
    private Portfolio portfolio;
    private TransactionArchive transactionArchive;

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

    public String getName() {
        return this.name;
    }

    public BigDecimal getMoney() {
        return this.money;
    }

    public void addMoney(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative");
        }
        this.money = this.money.add(amount);
    }

    public void withdrawMoney(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to withdraw cannot be negative");
        }
        this.money = this.money.subtract(amount);
    }

    public Portfolio getPortfolio() {
        return this.portfolio;
    }

    public TransactionArchive getTransactionArchive() {
        return this.transactionArchive;
    }

    /**
     * Returns the player's current status based on trading activity and net worth performance.
     * 
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
        if (weeksActive >= 20 && totalNetWorth.compareTo(this.startingMoney.multiply(new BigDecimal("2"))) >= 0) {
            return PlayerStatus.SPECULATOR;
        }
        
        // Check for INVESTOR status: 10+ weeks AND net worth increased by 20%
        if (weeksActive >= 10 && totalNetWorth.compareTo(this.startingMoney.multiply(new BigDecimal("1.2"))) >= 0) {
            return PlayerStatus.INVESTOR;
        }
        
        // Default: NOVICE
        return PlayerStatus.NOVICE;
    }

}
