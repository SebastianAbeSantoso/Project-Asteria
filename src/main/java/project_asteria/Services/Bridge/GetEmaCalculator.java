package project_asteria.Services.Bridge;

import project_asteria.Services.Calc.SmaCalc;

import java.sql.SQLException;

public interface GetEmaCalculator {
    public double getEma(String symbol, int period) throws SQLException;
}
