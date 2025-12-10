package project_asteria.Services.Bridge;

import java.sql.SQLException;

public interface GetEmaCalculator {
    double getEma(String symbol, int period) throws SQLException;
}
