package project_asteria.Services.Bridge;

import project_asteria.Model.BollingerBandsResult;
import project_asteria.Model.MacdResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IStockCalculationSuite {
    double getSma(String symbol, int period) throws SQLException, IOException;
    double getEma(String symbol, int period) throws SQLException, IOException;
    double getRsi(String symbol, int period) throws SQLException, IOException;
    MacdResult getMacd(String symbol, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
    BollingerBandsResult getBollingerBands(String symbol) throws SQLException;
    BollingerBandsResult getCustomBollingerBands(String symbol, int period, double stdDev) throws SQLException;
    List<Double> calculateATR(String symbol, int period) throws SQLException;
}
