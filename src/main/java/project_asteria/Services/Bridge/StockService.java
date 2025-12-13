package project_asteria.Services.Bridge;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;
import project_asteria.Repository.PriceHistoryRepository;
import project_asteria.Services.Calculators.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StockService implements IStockCalculationSuite {
    private final PriceHistoryRepository repository;
    private final ISmaCalculator smaCalc;
    private final IEmaCalculator emaCalc;
    private final IMacdCalculator macdCalc;
    private final IRsiCalculator rsiCalc;
    private final IBollingerBandsCalculator bollingerBandsCalc;
    private final AtrCalculator atrCalc;

    public StockService(PriceHistoryRepository repository, ISmaCalculator smaCalc, IEmaCalculator emaCalc, IMacdCalculator macdCalc, IRsiCalculator rsiCalc, IBollingerBandsCalculator bollingerBandsCalc, AtrCalculator atrCalc) {
        this.repository = repository;
        this.smaCalc = smaCalc;
        this.emaCalc = emaCalc;
        this.macdCalc = macdCalc;
        this.rsiCalc = rsiCalc;
        this.bollingerBandsCalc = bollingerBandsCalc;
        this.atrCalc = atrCalc;
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
        return macdCalc.macdCalculator(data, fastPeriod, slowPeriod, signalPeriod);
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

    public List<Double> calculateATR(String symbol, int period) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return atrCalc.calculateATR(data, period);
    }
}
