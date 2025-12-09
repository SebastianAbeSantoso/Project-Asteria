package project_asteria.Services.Calc;

import project_asteria.Model.MacdResult;
import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface MacdCalc {
    public MacdResult macdCalculator (List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
}
