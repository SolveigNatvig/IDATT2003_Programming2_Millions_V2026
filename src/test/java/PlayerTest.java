import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Jane", new BigDecimal("100"));
    }

    @Test
    void getName_shouldReturnCorrectName() {
        
        // Arrange
        String expectedName = "Jane";

        // Act
        String name = player.getName();

        // Assert
        assertEquals(expectedName, name);

    }

    @Test
    void getMoney_shouldReturnPlayersMoney() {

        // Arrange
        BigDecimal expectedMoney = new BigDecimal("100");

        // Act
        BigDecimal money = player.getMoney();

        // Assert 
        assertEquals(expectedMoney, money);
    }

    @Test
    void addMoney_givenAmount_shouldAddToPlayersMoney() {

        // Arrange
        BigDecimal moneyBefore = player.getMoney();
        BigDecimal amountToAdd = new BigDecimal("50");
        BigDecimal expectedMoneyAfter = moneyBefore.add(amountToAdd);

        // Act
        player.addMoney(amountToAdd);
        BigDecimal moneyAfter = player.getMoney();

        // Assert
        assertEquals(expectedMoneyAfter, moneyAfter);
    }

    @Test
    void withdrawMoney_givenAmount_shouldWithdrawFromPlayersMoney() {
        // Arrange
        BigDecimal moneyBefore = player.getMoney();
        BigDecimal amountToWithdraw = new BigDecimal("50");
        BigDecimal expectedMoneyAfter = moneyBefore.subtract(amountToWithdraw);

        // Act
        player.withdrawMoney(amountToWithdraw);
        BigDecimal moneyAfter = player.getMoney();

        // Assert
        assertEquals(expectedMoneyAfter, moneyAfter);
    }

    @Test
    void getPortfolio_shouldReturnPortfolio() {

    }

    @Test
    void getTransactionArchive_shouldReturnTransactionArchive() {

    }
    
}
