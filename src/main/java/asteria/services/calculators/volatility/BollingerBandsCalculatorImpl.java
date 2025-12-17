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
}
