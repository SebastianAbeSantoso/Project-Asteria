package asteria.services.insight;

import asteria.model.BollingerBandsResult;
import asteria.model.StochasticResult;
import asteria.services.bridge.StockCalculationSuite;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.lang.Math.abs;

public class InsightRulesImpl implements InsightRules {
    public enum Trend {UP, DOWN, SIDEWAYS}
    public enum Volatility {LOW, MEDIUM, HIGH}
    public enum Momentum {BULLISH, BEARISH, NEUTRAL}

    private final StockCalculationSuite stockCalculationSuite;

    public InsightRulesImpl(StockCalculationSuite stockCalculationSuite) {
        this.stockCalculationSuite = stockCalculationSuite;

    }

    public Trend getTrend (String symbol) throws SQLException, IOException {
        double sma20 = stockCalculationSuite.getSma(symbol, 20);
        double ema20 = stockCalculationSuite.getEma(symbol, 20);
        double hist = stockCalculationSuite.getMacd(symbol).getHistogram();

        if (sma20 == 0 ) return Trend.SIDEWAYS;

        double spread = abs(ema20 - sma20)/ sma20;
        final double WEAK_THRESHOLD = 0.005;
        final double STRONG_THRESHOLD = 0.005;

        if (spread < WEAK_THRESHOLD || (Math.abs(hist) < STRONG_THRESHOLD)) return Trend.SIDEWAYS;
        if (ema20 > sma20 && hist > STRONG_THRESHOLD) return Trend.UP;
        if (ema20 > sma20 && hist < -STRONG_THRESHOLD) return Trend.DOWN;
        return (ema20 > sma20) ? Trend.UP : Trend.DOWN;
    }

    public Momentum getMomentum(String symbol) throws SQLException, IOException {
        List<StochasticResult> stochList = stockCalculationSuite.getStandardStochastic(symbol);
        if (stochList == null || stochList.isEmpty()) return Momentum.NEUTRAL;

        double stochDValue = stochList.getLast().getdValue();
        double stochKValue = stochList.getLast().getkValue();

        double rsi14 = stockCalculationSuite.getRsi(symbol, 14);
        double hist = stockCalculationSuite.getMacd(symbol).getHistogram();

        if (rsi14 > 70 && stochKValue > 80 && stochDValue > stochKValue && hist < 0) return Momentum.BEARISH;
        if (30 > rsi14 && 20 > stochKValue && stochKValue > stochDValue && hist > 0) return Momentum.BULLISH;

        else return Momentum.NEUTRAL;
    }

    public Volatility getVolatility(String symbol) throws SQLException {
        BollingerBandsResult bb = stockCalculationSuite.getBollingerBands(symbol);
        if (bb == null) return Volatility.MEDIUM;
        //double atr14 = stockCalculationSuite.getAtr(symbol, 14).getLast(); kalo mau implementasi klasifikasi

        double price = bb.getMiddleBand();

        double bandwidth = (bb.getUpperBand() - bb.getLowerBand()) / price;

        final double LOW_THRESHOLD = 0.02;
        final double HIGH_THRESHOLD = 0.08;

        if (bandwidth < LOW_THRESHOLD) return Volatility.LOW;
        if (bandwidth > HIGH_THRESHOLD) return Volatility.HIGH;
        else return Volatility.MEDIUM;
    }

    public String getOverallInsight (String symbol) throws SQLException, IOException {
        Trend trend = getTrend(symbol);
        Momentum momentum = getMomentum(symbol);
        Volatility vol = getVolatility(symbol);
        return "Trend : " + trend + " momentum : " + momentum + " volatility : " + vol;

    }
}
