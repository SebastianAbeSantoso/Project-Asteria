package asteria.services.calculators.trend;

import asteria.model.MacdResult;
import asteria.model.PriceCandle;
import asteria.services.calculators.BaseCalculator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmaCalculatorImpl extends BaseCalculator implements EmaCalculator {
    private final double two = 2.0;
    private final double one = 1.0;

    @Override
    public double calculateEma(List<PriceCandle> candles, int period) {
        if (!hasEnoughData(candles, period)) return Double.NaN;

        List<Double> allCloses = getAllClosingPrices(candles);
        List<Double> firstN = allCloses.subList(0, period);

        double ema = calculateMean(firstN);

        double multiplier = two / (period + one);

        for (int i = period; i < allCloses.size(); i++) {
            double price = allCloses.get(i);
            ema = (price * multiplier) + (ema * (one - multiplier));
        }

        return ema;
    }

    @Override
    public List<Double> calculateEmaValue(List<Double> values, int period) {
        List<Double> emaHistory = new ArrayList<>();

        if (!hasEnoughData(values, period)) return emaHistory;

        List<Double> firstN = values.subList(0, period);
        double currentEma = calculateMean(firstN);

        for (int i = 0; i < period - 1; i++) emaHistory.add(0.0);

        emaHistory.add(currentEma);

        double multiplier = two / (period + one);

        for (int i = period; i < values.size(); i++) {
            double price = values.get(i);
            currentEma = (price * multiplier) + (currentEma * (one - multiplier));
            emaHistory.add(currentEma);
        }

        return emaHistory;
    }
}