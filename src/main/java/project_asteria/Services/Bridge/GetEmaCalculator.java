package project_asteria.Services.Bridge;

import java.sql.SQLException;

public interface GetEmaCalculator {
    public double getEma(String symbol, int period) throws SQLException;
}
