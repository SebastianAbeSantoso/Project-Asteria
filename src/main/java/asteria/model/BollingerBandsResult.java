package asteria.model;

public class BollingerBandsResult {

    private double upperBand;
    private double middleBand;
    private double lowerBand;

    public BollingerBandsResult(double upperBand, double middleBand, double lowerBand) {
        this.upperBand = upperBand;
        this.middleBand = middleBand;
        this.lowerBand = lowerBand;
    }

    public double getUpperBand() {
        return upperBand;
    }

    public double getMiddleBand() {
        return middleBand;
    }

    public double getLowerBand() {
        return lowerBand;
    }

    @Override
    public String toString() {
        return "Bollinger Band [Lower] = " + lowerBand + " [Middle] = " + middleBand + " [Upper] = " + upperBand + "]";
    }
}
