package asteria.services.bridge;

import asteria.model.*;
import asteria.repository.PriceHistoryRepository;
import asteria.services.calculators.momentum.RsiCalculator;
import asteria.services.calculators.momentum.StochasticCalculator;
import asteria.services.calculators.trend.EmaCalculator;
import asteria.services.calculators.trend.MacdCalculator;
import asteria.services.calculators.trend.SmaCalculator;
import asteria.services.calculators.volatility.AtrCalculator;
import asteria.services.calculators.volatility.BollingerBandsCalculator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StockService implements StockCalculationSuite {
    private final PriceHistoryRepository repository;
    private final SmaCalculator smaCalc;
    private final EmaCalculator emaCalc;
    private final MacdCalculator macdCalc;
    private final RsiCalculator rsiCalc;
    private final BollingerBandsCalculator bollingerBandsCalc;
    private final AtrCalculator atrCalc;
    private final StochasticCalculator stochCalc;

    public StockService(PriceHistoryRepository repository, SmaCalculator smaCalc, EmaCalculator emaCalc, MacdCalculator macdCalc, RsiCalculator rsiCalc, BollingerBandsCalculator bollingerBandsCalc, AtrCalculator atrCalc, StochasticCalculator stochCalc) {
        this.repository = repository;
        this.smaCalc = smaCalc;
        this.emaCalc = emaCalc;
        this.macdCalc = macdCalc;
        this.rsiCalc = rsiCalc;
        this.bollingerBandsCalc = bollingerBandsCalc;
        this.atrCalc = atrCalc;
        this.stochCalc = stochCalc;
    }

    public double getSma(String symbol, int period) throws SQLException, IOException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return smaCalc.calculateSma(data, period);
    }

    public double getEma(String symbol, int period) throws SQLException, IOException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return emaCalc.calculateEma(data, period);
    }

    public MacdResult getMacd(String symbol, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return macdCalc.calculateMacd(data, fastPeriod, slowPeriod, signalPeriod);
    }

    public double getRsi(String symbol, int period) throws SQLException, IOException {
        List<PriceCandle> data = repository.loadCandles(symbol);
         return rsiCalc.calculateRsi(data, period);
    }

    public BollingerBandsResult getBollingerBands(String symbol) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return bollingerBandsCalc.calculateStandardBollingerBands(data);
    }

    public BollingerBandsResult getCustomBollingerBands(String symbol, int period, double stdDev) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return bollingerBandsCalc.calculateBollingerBands(data, period, stdDev);
    }

    public List<Double> getAtr(String symbol, int period) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return atrCalc.calculateAtr(data, period);
    }

    public List<StochasticResult> getStandardStochastic(String symbol) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return stochCalc.getStandardStochastic(data);
    }

    public List<StochasticResult> getStochastic(String symbol, int kPeriod, int dPeriod) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);

        return stochCalc.getStochastic(data, kPeriod, dPeriod);
    }

    public MarketSnapshot getFullAnalysis(String symbol) throws SQLException, IOException {
        List<PriceCandle> data = repository.loadCandles(symbol);

        if (data == null || data.isEmpty()) {
            return null;
        }

        double currentPrice = data.getLast().getClose();

        double rsi = rsiCalc.calculateRsi(data, 14);
        double sma = smaCalc.calculateSma(data, 20);
        double ema = emaCalc.calculateEma(data, 20);
        List<Double> atrList = atrCalc.calculateAtr(data, 14);
        double atr = atrList.isEmpty() ? 0.0 : atrList.getLast();
        MacdResult macd = macdCalc.calculateMacd(data);
        BollingerBandsResult bb = bollingerBandsCalc.calculateBollingerBands(data, 20, 2.0);
        List<StochasticResult> stochList = stochCalc.getStandardStochastic(data);

        StochasticResult stoch = stochList.isEmpty() ? new StochasticResult(0,0) : stochList.getLast();
        return new MarketSnapshot(
                symbol,
                currentPrice,
                rsi,
                sma,
                ema,
                atr,
                macd,
                bb,
                stoch

        );
    }
}
