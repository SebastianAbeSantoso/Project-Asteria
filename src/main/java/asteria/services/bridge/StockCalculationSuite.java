package asteria.services.bridge;

import asteria.model.BollingerBandsResult;
import asteria.model.MacdResult;
import asteria.model.MarketSnapshot;
import asteria.model.StochasticResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface StockCalculationSuite {
    double getSma(String symbol, int period) throws SQLException, IOException;
    double getEma(String symbol, int period) throws SQLException, IOException;
    double getRsi(String symbol, int period) throws SQLException, IOException;
    MacdResult getMacd(String symbol, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
    BollingerBandsResult getBollingerBands(String symbol) throws SQLException;
    BollingerBandsResult getCustomBollingerBands(String symbol, int period, double stdDev) throws SQLException;
    List<Double> getAtr(String symbol, int period) throws SQLException;
    List<StochasticResult> getStochastic(String symbol, int kPeriod, int dPeriod) throws SQLException, IOException;
    List<StochasticResult> getStandardStochastic(String symbol) throws SQLException;
    MarketSnapshot getFullAnalysis(String symbol) throws SQLException, IOException;
}
