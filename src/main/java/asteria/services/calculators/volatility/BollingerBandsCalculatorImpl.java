package asteria.services.calculators.volatility;

import asteria.model.BollingerBandsResult;
import asteria.model.PriceCandle;
import asteria.services.calculators.BaseCalculator;

import java.util.ArrayList;
import java.util.List;

public class BollingerBandsCalculatorImpl extends BaseCalculator implements BollingerBandsCalculator {

    public BollingerBandsResult calculateBollingerBands (List<PriceCandle> candles, int period, double numStdDev){
        if (!hasEnoughData(candles, period)) return new BollingerBandsResult(Double.NaN, Double.NaN, Double.NaN);


        List<Double> closes = getLastNClosingPrices(candles, period);

        double middleBand = calculateMean(closes);

        double varianceSum = 0.0;

        for (double close : closes) {
            varianceSum += Math.pow(close - middleBand, 2);
        }

        double variance = varianceSum/period;
        double stdDev = Math.sqrt(variance);

        double upperBand = middleBand + (stdDev * numStdDev);
        double lowerBand = middleBand - (stdDev * numStdDev);

        return new BollingerBandsResult(upperBand, middleBand, lowerBand);
    }

    public BollingerBandsResult calculateStandardBollingerBands (List<PriceCandle> candles){
        return calculateBollingerBands(candles, 20, 2.0);
    }

    public enum BollingerPosition {
        BELOW_LOWER,
        BETWEEN_LOWER_MIDDLE,
        BETWEEN_MIDDLE_UPPER,
        ABOVE_UPPER,
        UNKNOWN
    }

    public BollingerPosition classifyBollingerPosition(double lastClose,
                                                       BollingerBandsResult bands) {
        if (Double.isNaN(bands.getLowerBand()) ||
                Double.isNaN(bands.getMiddleBand()) ||
                Double.isNaN(bands.getUpperBand())) {
            return BollingerPosition.UNKNOWN;
        }

        double lower = bands.getLowerBand();
        double middle = bands.getMiddleBand();
        double upper = bands.getUpperBand();

        if (lastClose < lower) return BollingerPosition.BELOW_LOWER;
        if (lastClose > upper) return BollingerPosition.ABOVE_UPPER;
        if (lastClose < middle) return BollingerPosition.BETWEEN_LOWER_MIDDLE;
        if (lastClose >= middle && lastClose <= upper)
            return BollingerPosition.BETWEEN_MIDDLE_UPPER;

        return BollingerPosition.UNKNOWN;
    }
}
