package asteria.model;

public class StochasticResult {
    private final double kValue;
    private final double dValue;

    public StochasticResult(double kValue, double dValue) {
        this.kValue = kValue;
        this.dValue = dValue;
    }

    public double getdValue() {
        return dValue;
    }

    public double getkValue() {
        return kValue;
    }

    @Override
    public String toString() {
        return String.format("Stoch K: %.2f | D: %.2f", kValue, dValue);
    }
}
