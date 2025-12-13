package project_asteria.Services.Calculators;

import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IMacdCalculator {
    MacdResult macdCalculator (List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
}
