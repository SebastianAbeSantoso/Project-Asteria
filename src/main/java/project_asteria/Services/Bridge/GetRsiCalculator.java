package project_asteria.Services.Bridge;

import project_asteria.Model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface GetRsiCalculator {
    double getRsi(String symbol, int period) throws SQLException, IOException;
}
