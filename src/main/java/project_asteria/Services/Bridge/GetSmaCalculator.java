package project_asteria.Services.Bridge;

import java.sql.SQLException;

public interface GetSmaCalculator {
    double getSma(String symbol, int period) throws SQLException;
}
