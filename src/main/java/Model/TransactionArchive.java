package Model;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class TransactionArchive {
    private List<Transaction> transactions;

    public TransactionArchive() {
        this.transactions = new ArrayList<>();
    }

    public boolean add(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Should not be null"); // Eller NullPointerExeption?
        }
        
        return transactions.add(transaction);

    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public List<Transaction> getTransactions(int week) {
        return transactions.stream().filter(transaction -> transaction.getWeek() == week).collect(Collectors.toList());
    }

    public List<Purchase> getPurchase(int week) {
        return transactions.stream().filter(transaction -> transaction.getWeek() == week).filter(purchase -> purchase instanceof Purchase).map(transaction -> (Purchase) transaction).collect(Collectors.toList());
    }

    public List<Sale> getSale(int week) {
        return transactions.stream().filter(transaction -> transaction.getWeek() == week).filter(sale -> sale instanceof Sale).map(transaction -> (Sale) transaction).collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public int countDistinctWeeks() {
        return (int) transactions.stream().map(Transaction::getWeek).distinct().count();
    }
}
