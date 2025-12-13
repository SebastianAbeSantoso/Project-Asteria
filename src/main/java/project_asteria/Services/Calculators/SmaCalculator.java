package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.util.List;

public class SmaCalculator implements ISmaCalculator {

    public double calculateSma (List<PriceCandle> candles, int period){
        if (candles == null || candles.size() < period) {
            return Double.NaN;
        }

        double sum = 0;

        int startIndex = candles.size() - period;
        for  (int i = startIndex; i < candles.size(); i++) {
            sum += candles.get(i).getClose();
        }

        return sum / period;
    }

}
