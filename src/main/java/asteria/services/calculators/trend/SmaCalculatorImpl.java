package asteria.services.calculators.trend;

import asteria.model.PriceCandle;
import asteria.services.calculators.BaseCalculator;

import java.util.List;

public class SmaCalculatorImpl extends BaseCalculator implements SmaCalculator {

    public double calculateSma (List<PriceCandle> candles, int period){
        if (!hasEnoughData(candles, period)) return Double.NaN;

        List<Double> closes = getLastNClosingPrices(candles, period);

        return calculateMean(closes);
    }

}
