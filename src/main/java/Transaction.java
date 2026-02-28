public abstract class Transaction {
    private Share share;
    private int week;
    private TransactionCalculator calculator;
    protected boolean committed;

    protected Transaction(Share share, int week, TransactionCalculator calculator) {
        this.share = share;
        this.week = week;
        this.calculator = calculator;
    }

    public Share getShare() {
        return this.share;
    }

    public int getWeek() {
        return this.week;
    }

    public TransactionCalculator getCalculator() {
        return this.calculator;
    }

    public boolean isCommitted() {
        return this.committed;
    }

    public abstract void commit(Player player);
    
}
