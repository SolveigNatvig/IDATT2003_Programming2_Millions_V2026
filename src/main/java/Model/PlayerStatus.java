package Model;

/**
 * Enum representing the player's status level based on trading activity and performance.
 * 
 * Status levels:
 * - NOVICE: Starting level, no requirements
 * - INVESTOR: Traded for at least 10 weeks AND increased net worth by at least 20%
 * - SPECULATOR: Traded for at least 20 weeks AND doubled the net worth (100% increase)
 */
public enum PlayerStatus {
    NOVICE("Novice"),
    INVESTOR("Investor"),
    SPECULATOR("Speculator");

    private final String displayName;

    PlayerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
