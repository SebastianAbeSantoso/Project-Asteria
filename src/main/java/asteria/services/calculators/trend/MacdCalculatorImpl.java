package asteria.services.calculators.trend;

import asteria.model.MacdResult;
import asteria.model.PriceCandle;
import asteria.services.calculators.BaseCalculator;

import java.util.ArrayList;
import java.util.List;

public class MacdCalculatorImpl extends BaseCalculator implements MacdCalculator {
    private final double two = 2;
    private final double one = 1;
    private final EmaCalculator emaCalc;
    private double macdLine;
    private double signalLine;
    private double histogramLine;

    public MacdCalculatorImpl(EmaCalculator emaCalc) {
        this.emaCalc = emaCalc;
    }

    public MacdResult calculateMacd(List<PriceCandle> candles) {
        return calculateMacd(candles, 12, 26, 9);
    }

    public MacdResult calculateMacd (List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (!hasEnoughData(candles, slowPeriod + signalPeriod)) return new MacdResult(Double.NaN, Double.NaN, Double.NaN);


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

    public double getHistogramLine() {
        return histogramLine;
    }

    public double getSignalLine() {
        return signalLine;
    }

    public double getMacdLine() {
        return macdLine;
    }
}
