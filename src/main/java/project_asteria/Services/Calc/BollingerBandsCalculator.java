package project_asteria.Services.Calc;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.PriceCandle;

import java.util.List;

public class BollingerBandsCalculator implements BollingerBandsCalc {

    public BollingerBandsResult calculateBollingerBands (List<PriceCandle> candles, int period, double numStdDev){
        if (candles == null || candles.size() < period){
            return new BollingerBandsResult(Double.NaN, Double.NaN, Double.NaN);
        }

        int size = candles.size();
        int startIndex = 0;
        int endIndex = size - 1;

        double sum = 0.0;
        for (int i = startIndex; i < size; i++){
            sum += candles.get(i).getClose();
        }

        double mean = sum/period;

        double varianceSum = 0.0;

        for (int i = startIndex; i < size; i++){
            double diff = candles.get(i).getClose() - mean;
            varianceSum += diff * diff;
        }

        double variance = varianceSum/period;
        double stdDev = Math.sqrt(variance);

        double upper = mean + numStdDev * stdDev;
        double lower = mean - numStdDev * stdDev;

        return new BollingerBandsResult(upper, mean, lower);
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
