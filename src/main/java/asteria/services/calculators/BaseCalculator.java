package asteria.services.calculators;

import asteria.model.PriceCandle;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCalculator {
    protected boolean hasEnoughData(List<?> list, int requiredSize) {
        return list != null && list.size() >= requiredSize;
    }

    protected List<Double> getLastNClosingPrices(List<PriceCandle> candles, int n) {
        List<Double> closes = new ArrayList<>();
        int startIndex = candles.size() - n;

        if (startIndex < 0) startIndex = 0;

        for (int i = startIndex; i < candles.size(); i++) {
            closes.add(candles.get(i).getClose());
        }
        return closes;
    }

    protected List<Double> getAllClosingPrices(List<PriceCandle> candles) {
        List<Double> closes = new ArrayList<>();
        for (PriceCandle c : candles) {
            closes.add(c.getClose());
        }
        return closes;
    }

    protected double calculateMean(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.size();
    }


}
