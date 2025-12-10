package project_asteria.Services.Bridge;

import project_asteria.Model.PriceCandle;

import java.sql.SQLException;
import java.util.List;

public interface GetAtrCalculator {
    List<Double> calculateATR(String symbol, int period) throws SQLException;
}
