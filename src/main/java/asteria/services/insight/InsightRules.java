package asteria.services.insight;

import asteria.model.BollingerBandsResult;
import asteria.model.MacdResult;
import asteria.services.bridge.StockCalculationSuite;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.lang.Math.abs;

public class InsightRules {
    public enum Trend {UP, DOWN, SIDEWAYS}
    public enum Volatility {LOW, MEDIUM, HIGH}
    public enum Momentum {BULLISH, BEARISH, NEUTRAL}

    private final StockCalculationSuite stockCalculationSuite;

    public InsightRules(StockCalculationSuite stockCalculationSuite) {
        this.stockCalculationSuite = stockCalculationSuite;
    }

    public Trend getTrend (String symbol, int period) {
        double sma = stockCalculationSuite.getSma(symbol, period);
        double ema = stockCalculationSuite.getEma(symbol, period);

        if (sma == 0 ) return Trend.SIDEWAYS;

        double spread = abs(ema - sma)/sma;
        double threshold = 0.005;

        if (spread < threshold) return Trend.SIDEWAYS;
        return (ema > sma) ? Trend.UP : Trend.DOWN;
    }

    public Momentum getMomentum(String symbol) throws SQLException, IOException {
        double hist = stockCalculationSuite.getMacd(symbol).getHistogram();

        if (hist == 0 || histi can< 2) return Momentum.BEARISH;

    }
}
