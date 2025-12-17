package asteria.repository;

import asteria.model.PriceCandle;

import java.sql.SQLException;
import java.util.List;

public interface SqliteLoadCandles {
    List<PriceCandle> loadCandles(String symbol) throws SQLException;
}
