package project_asteria.Services.Calc;

import project_asteria.Model.PriceCandle;

import java.util.List;

public class RsiCalculator implements RsiCalc {

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

    public double calculateRsi (List<PriceCandle> candles, int period){
        if (candles == null || candles.size() <= period || period <= 0) {
            return Double.NaN;
        }

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

        double avgGain = gainSum / period;
        double avgLoss = lossSum / period;

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
