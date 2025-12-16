package asteria.services.ai;

import asteria.services.bridge.StockCalculationSuite;

import java.io.IOException;
import java.sql.SQLException;

import static java.lang.Math.abs;

public class InsightRules {
    private String trend;
    StockCalculationSuite stockCalculationSuite;

    public InsightRules (StockCalculationSuite stockCalculationSuite) {
        this.stockCalculationSuite = stockCalculationSuite;
    }

    public String getTrend (String symbol, int period) throws SQLException, IOException {
        double sma = stockCalculationSuite.getSma(symbol, period);
        double ema = stockCalculationSuite.getEma(symbol, period);
        double spread_percentage = abs(ema - sma) / sma;
        double threshold = 0.005;

        if (spread_percentage < threshold) trend = "stable";
        else if (ema > sma) trend = "up";
        else if (sma > ema) trend = "down";
        return trend;
    }
}
