package project_asteria.Services.Bridge;

import project_asteria.Model.BollingerBandsResult;

import java.sql.SQLException;

public interface GetBollingerBandsCalculator {
    BollingerBandsResult getBollingerBands(String symbol) throws SQLException;

    BollingerBandsResult getCustomBollingerBands(String symbol, int period, double stdDev) throws SQLException;
}
