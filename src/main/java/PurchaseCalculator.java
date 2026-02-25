import java.math.BigDecimal;

public class PurchaseCalculator {
    private BigDecimal purchasePrice;
    private BigDecimal quantity;
    
    public PurchaseCalculator(Share share) {
        this.purchasePrice = share.getPurchasePrice();
        this.quantity = share.getQuantity();
    }

    public BigDecimal calculateGross() {
        return this.purchasePrice.multiply(this.quantity);
    }

    public BigDecimal calculateCommission() {
        BigDecimal rate = new BigDecimal("0.005");
        return calculateGross().multiply(rate);
    }

    public BigDecimal calculateTax() {
        BigDecimal tax = new BigDecimal("0");
        return tax;
    }

    public BigDecimal calculateTotal() {
        return calculateGross().add(calculateCommission()).add(calculateTax());
    }
}
