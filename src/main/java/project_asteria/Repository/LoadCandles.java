package project_asteria.Repository;

import project_asteria.Model.PriceCandle;

import java.sql.SQLException;
import java.util.List;

public interface LoadCandles {
    public List<PriceCandle> loadCandles(String symbol) throws SQLException;
}
