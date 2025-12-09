package project_asteria.Services.Bridge;

import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;
import project_asteria.Repository.PriceHistoryRepository;
import project_asteria.Services.Calc.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StockService implements GetSmaCalculator, GetEmaCalculator, GetMacdCalculator, GetRsiCalculator {
    private final PriceHistoryRepository repository;
    private final SmaCalculator smaCalc;
    private final EmaCalculator emaCalc;
    private final MacdCalc macdCalc;
    private final RsiCalc rsiCalc;

    public StockService(PriceHistoryRepository repository, SmaCalculator smaCalc, EmaCalculator emaCalc, MacdCalc macdCalc, RsiCalc rsiCalc) {
        this.repository = repository;
        this.smaCalc = smaCalc;
        this.emaCalc = emaCalc;
        this.macdCalc = macdCalc;
        this.rsiCalc = rsiCalc;
    }

    public double getSma(String symbol, int period) throws SQLException {
        List<PriceCandle> data = repository.loadCandles(symbol);
        return smaCalc.calculateSma(data, period);
    }

    public double getEma(String symbol, int period) throws SQLException {
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
}
