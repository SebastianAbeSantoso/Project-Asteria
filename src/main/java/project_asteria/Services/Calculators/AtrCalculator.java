package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.util.ArrayList;
import java.util.List;

public class AtrCalculator implements IAtrCalculator {

    public List<Double> calculateATR(List<PriceCandle> candles, int period){
        List<Double> atrValues = new ArrayList<>();
        List<Double> trueRanges = new ArrayList<>();

        if (candles.size() < period + 1){
            return atrValues;
        }

        for (int i = 1; i < candles.size(); i++){
            PriceCandle today =  candles.get(i);
            PriceCandle yesterday =  candles.get(i-1);

            double highLow = today.getHigh() - today.getLow();
            double highPrevClose = Math.abs(today.getHigh() - yesterday.getClose());
            double lowPrevClose = Math.abs(today.getLow() - yesterday.getClose());

            double tr = Math.max(highLow, Math.max(highPrevClose, lowPrevClose));
            trueRanges.add(tr);
        }

        double initialSum = 0.0;
        for (int i = 0; i < period; i++) {
            initialSum += trueRanges.get(i);
        }

        double atr = initialSum / period;
        atrValues.add(atr);

        for (int i = period; i < trueRanges.size(); i++){
            double currentTr = trueRanges.get(i);
            atr = ((atr * (period - 1)) + currentTr) / period;
            atrValues.add(atr);
        }
        double todayATR = atrValues.get(atrValues.size() - 1);
        return atrValues;
    }
}
