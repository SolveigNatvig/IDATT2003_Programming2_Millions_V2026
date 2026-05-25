package Model.Player;

/**
 * Enum representing the player's trading status level.
 * Status levels are determined by trading activity (weeks with transactions)
 * and net worth performance compared to starting capital.
 * Levels:
 * - NOVICE: Initial status (default)
 * - INVESTOR: 10+ weeks of trading AND net worth increased by 20%
 * - SPECULATOR: 20+ weeks of trading AND net worth doubled
 */
public enum PlayerStatus {
    NOVICE("Novice"),
    INVESTOR("Investor"),
    SPECULATOR("Speculator");

  private final String displayName;

  /**
   * Constructs a PlayerStatus with the given display name.
   *
   * @param displayName the name for UI display
   */
  PlayerStatus(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the display name for this status.
   *
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }
}
