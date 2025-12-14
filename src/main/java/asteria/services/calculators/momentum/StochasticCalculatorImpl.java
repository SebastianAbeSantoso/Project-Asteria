package asteria.services.calculators.momentum;

import asteria.model.PriceCandle;
import asteria.model.StochasticResult;
import asteria.services.calculators.BaseCalculator;

import java.util.ArrayList;
import java.util.List;

public class StochasticCalculatorImpl extends BaseCalculator implements StochasticCalculator {

    public List<StochasticResult> getStochastic(List<PriceCandle> candles, int kPeriod, int dPeriod) {
        if (!hasEnoughData(candles, kPeriod + dPeriod)) return new ArrayList<>();


        List<Double> rawKValues = calculateRawKValues(candles, kPeriod);
        return calculateStochasticResults(rawKValues, dPeriod);
    }

    private List<Double> calculateRawKValues(List<PriceCandle> candles, int kPeriod) {
        List<Double> kValues = new ArrayList<>();

        for (int i = kPeriod - 1; i < candles.size(); i++) {
            double currentClose = candles.get(i).getClose();

            double lowestLow = getLowestLow(candles, i, kPeriod);
            double highestHigh = getHighestHigh(candles, i, kPeriod);

            double range = highestHigh - lowestLow;
            double k = (range == 0.0) ? 50.0 : 100.0 * ((currentClose - lowestLow) / range);

            kValues.add(k);
        }
        return kValues;
    }

    private List<StochasticResult> calculateStochasticResults(List<Double> rawKValues, int dPeriod) {
        List<StochasticResult> results = new ArrayList<>();

        for (int i = dPeriod - 1; i < rawKValues.size(); i++) {
            double sum = 0.0;
            for (int j = 0; j < dPeriod; j++) {
                sum += rawKValues.get(i-j);
            }

            double d = sum / dPeriod;
            double k = rawKValues.get(i);

            results.add(new StochasticResult(k, d));
        }
        return results;
    }

    private double getLowestLow(List<PriceCandle> candles, int currentIndex, int lookback) {
        double min = Double.MAX_VALUE;
        for (int j = 0; j < lookback; j++) {
            double low = candles.get(currentIndex - j).getLow();
            if (low < min) min = low;
        }
        return min;
    }

    private double getHighestHigh(List<PriceCandle> candles, int currentIndex, int lookback) {
        double max = Double.MIN_VALUE;
        for (int j = 0; j < lookback; j++) {
            double high = candles.get(currentIndex - j).getHigh();
            if (high > max) max = high;
        }
        return max;
    }

    public List<StochasticResult> getStandardStochastic(List<PriceCandle> candles) {
        return getStochastic(candles, 14, 3);
    }
}
