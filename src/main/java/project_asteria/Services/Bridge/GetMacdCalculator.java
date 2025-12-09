package project_asteria.Services.Bridge;

import project_asteria.Model.MacdResult;
import project_asteria.Services.Calc.EmaCalcValue;

import java.io.IOException;
import java.sql.SQLException;

public interface GetMacdCalculator {
    public MacdResult getMacd(String symbol, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
}
