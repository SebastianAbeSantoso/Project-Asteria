package asteria.services.calculators.trend;

import asteria.model.MacdResult;
import asteria.model.PriceCandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface MacdCalculator {
    MacdResult calculateMacd(List<PriceCandle> candles, int fastPeriod, int slowPeriod, int signalPeriod) throws SQLException, IOException;
    MacdResult calculateMacd(List<PriceCandle> candles) throws SQLException, IOException;

}
