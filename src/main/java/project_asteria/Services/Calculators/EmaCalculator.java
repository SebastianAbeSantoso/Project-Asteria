package project_asteria.Services.Calculators;

import project_asteria.Model.PriceCandle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmaCalculator implements IEmaCalculator {
    private final double two = 2.0;
    private final double one = 1.0;

    public double calculateEma (List<PriceCandle> candles, int period) throws SQLException {
        if (candles.size() < period) {
            return Double.NaN;
        }

        double multiplier = two / (period + one);

        double sum = 0.0;
        for (int i = 0; i < period; i++) {
            sum += candles.get(i).getClose();
        }

        double ema = sum / period;

        int startIndex = candles.size() - period;
        for (int i = period; i < candles.size(); i++) {
            double price = candles.get(i).getClose();
            ema = (price * multiplier) + (ema * (one - multiplier));
        }

        return ema;
    }

    public List<Double> calculateEmaValue(List<Double> values, int period) {
        List<Double> emaHistory = new ArrayList<Double>();

        if (values.isEmpty() || values.size() < period) {
            return emaHistory;
        }

        double multiplier = two / (period + one);

        double sum = 0.0;
        for (int i = 0; i < period; i++) {
            sum += values.get(i);
        }

        double currentEma = sum / period;

        for(int i=0; i<period-1; i++) emaHistory.add(0.0);

        emaHistory.add(currentEma);

        for (int i = period; i < values.size(); i++) {
            double price = values.get(i);
            currentEma = (price * multiplier) + (currentEma * (1.0 - multiplier));
            emaHistory.add(currentEma);
        }

        return emaHistory;
    }

}
