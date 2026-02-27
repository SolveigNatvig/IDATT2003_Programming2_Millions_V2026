import java.math.BigDecimal;

public class SaleCalculator implements TransactionCalculator{
    private BigDecimal purchasePrice;
    private BigDecimal salesPrice;
    private BigDecimal quantity;

    public SaleCalculator(Share share) {
        this.purchasePrice = share.getPurchasePrice();
        this.salesPrice = share.getStock().getSalesPrice(); 
        this.quantity = share.getQuantity();
    }

    public BigDecimal calculateGross() {
        return this.salesPrice.multiply(this.quantity);
    }

    public BigDecimal calculateCommission() {
        BigDecimal rate = new BigDecimal("0.01");
        return calculateGross().multiply(rate);
    }

    public BigDecimal calculateTax() {
        BigDecimal sellingCost = this.purchasePrice.multiply(this.quantity);
        BigDecimal profit = calculateGross().subtract(calculateCommission()).subtract(sellingCost);
        BigDecimal rate = new BigDecimal("0.3");
        return profit.multiply(rate);
    }

    public BigDecimal calculateTotal() {
        return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
    }
    


}
