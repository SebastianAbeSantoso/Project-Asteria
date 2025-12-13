package project_asteria.Services.Calculators;

import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;

import java.util.ArrayList;
import java.util.List;

public class MacdCalculator implements IMacdCalculator {
    private final double two = 2;
    private final double one = 1;
    private final IEmaCalculator emaCalc;
    private double macdLine;
    private double signalLine;
    private double histogramLine;

    public MacdCalculator(IEmaCalculator emaValue) {
        this.emaCalc = emaValue;
    }

    public MacdResult macdCalculator (List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (candles == null || candles.size() < slowPeriod + signalPeriod) {
            return new MacdResult(Double.NaN, Double.NaN, Double.NaN);
        }

        List<Double> closePrices = new ArrayList<>();
        for (PriceCandle c : candles) closePrices.add(c.getClose());

        List<Double> fastEma = emaCalc.calculateEmaValue(closePrices, fastPeriod);
        List<Double> slowEma = emaCalc.calculateEmaValue(closePrices, slowPeriod);

        List<Double> macdLineHistory = new ArrayList<>();
        for (int i = 0; i < closePrices.size(); i++) {
            double fast = fastEma.get(i);
            double slow = slowEma.get(i);
            macdLineHistory.add(fast - slow);
        }

        List<Double> signalLineHistory = emaCalc.calculateEmaValue(macdLineHistory, 9);
        int lastIndex = closePrices.size() - 1;
        macdLine = macdLineHistory.get(lastIndex);
        signalLine = signalLineHistory.get(lastIndex);
        histogramLine = macdLine - signalLine;

        return new MacdResult(macdLine, signalLine, histogramLine);
    }
}
