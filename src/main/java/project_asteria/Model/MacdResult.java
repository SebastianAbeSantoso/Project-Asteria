package project_asteria.Model;

public class MacdResult {
    private final double macd;
    private final double signal;
    private final double histogram;

    public MacdResult(double macd, double signal, double histogram) {
        this.macd = macd;
        this.signal = signal;
        this.histogram = histogram;
    }

    public double getMacd() {
        return macd;
    }

    public double getSignal() {
        return signal;
    }

    public double getHistogram() {
        return histogram;
    }

    @Override
    public String toString() {
        return "MACD = " + macd + ", signal = " + signal + ", histogram = " + histogram;
    }
}
