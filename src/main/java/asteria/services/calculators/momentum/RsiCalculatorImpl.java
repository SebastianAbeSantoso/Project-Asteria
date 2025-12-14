package asteria.services.calculators.momentum;

import asteria.model.PriceCandle;
import asteria.services.calculators.BaseCalculator;

import java.util.List;

public class RsiCalculatorImpl extends BaseCalculator implements RsiCalculator {

    private record Averages(double avgGain, double avgLoss) {}

    public enum RsiState {
        OVERSOLD,
        NEUTRAL,
        OVERBOUGHT
    }

    public RsiState classifyRsi(double rsi) {
        if (Double.isNaN(rsi)) return RsiState.NEUTRAL;

        if (rsi < 30.0) return RsiState.OVERSOLD;
        if (rsi > 70.0) return RsiState.OVERBOUGHT;
        return RsiState.NEUTRAL;
    }

    private Averages calculateInitialAverage (List<PriceCandle> candles, int period) {
        double gainSum = 0.0;
        double lossSum = 0.0;

        for (int i = 0; i < period; i++) {
            double change = candles.get(i + 1).getClose() - candles.get(i).getClose();
            if (change > 0) {
                gainSum += change;
            } else {
                lossSum += -change;
            }
        }

        return new Averages(gainSum / period, lossSum / period);
    }

    public double calculateRsi (List<PriceCandle> candles, int period) {
        if (!hasEnoughData(candles, period)) return 0.0;


        Averages initialAverage = calculateInitialAverage(candles, period);
        double avgGain = initialAverage.avgGain;
        double avgLoss = initialAverage.avgLoss;

        for (int i = period; i < candles.size() - 1; i++) {
            double change = candles.get(i + 1).getClose() - candles.get(i).getClose();

            double currentGain = (change > 0 ) ? change : 0.0;
            double currentLoss = (change < 0 ) ? -change : 0.0;

            avgGain = ((avgGain * (period - 1) + currentGain) / period);
            avgLoss = ((avgLoss * (period - 1) + currentLoss) / period);
        }

        double rs = avgGain / avgLoss;

        return 100.0 - (100.0 / (1.0 + rs));
    }
}
