package project_asteria.Services.Calc;

import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;

import java.util.ArrayList;
import java.util.List;

public class MacdCalculator implements MacdCalc{
    private final double two = 2;
    private final double one = 1;
    private final EmaCalcValue emaValue;
    private double macdLine;
    private double signalLine;
    private double histogramLine;

    public MacdCalculator(EmaCalcValue emaValue) {
        this.emaValue = emaValue;
    }

    public MacdResult macdCalculator (List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (candles == null || candles.size() < slowPeriod + signalPeriod) {
            return new MacdResult(Double.NaN, Double.NaN, Double.NaN);
        }

        List<Double> closePrices = new ArrayList<>();
        for (PriceCandle c : candles) closePrices.add(c.getClose());

        List<Double> fastEma = emaValue.calculateEmaValue(closePrices, fastPeriod);
        List<Double> slowEma = emaValue.calculateEmaValue(closePrices, slowPeriod);

        List<Double> macdLineHistory = new ArrayList<>();
        for (int i = 0; i < closePrices.size(); i++) {
            double fast = fastEma.get(i);
            double slow = slowEma.get(i);
            macdLineHistory.add(fast - slow);
        }

        List<Double> signalLineHistory = emaValue.calculateEmaValue(macdLineHistory, 9);
        int lastIndex = closePrices.size() - 1;
        macdLine = macdLineHistory.get(lastIndex);
        signalLine = signalLineHistory.get(lastIndex);
        histogramLine = macdLine - signalLine;

        return new MacdResult(macdLine, signalLine, histogramLine);
    }
}
